import { ExecutionResult } from '@graphql-tools/utils';
import { DocumentNode, print } from 'graphql';
import { createClient, Sink } from 'graphql-ws';
import ws from 'ws';

interface Payload {
  document: DocumentNode;
  variables: Record<string, any> | undefined;
}

// Signature of the function `createRemoteSubscriber` returns
export type CreateRemoteSubscriber<T> = (payload: Payload) => Promise<AsyncIterableIterator<ExecutionResult<T>>>;

/**
 * Builds an AsyncIterator wrapper around a WS client, see <https://github.com/enisdenjo/graphql-ws#async-iterator>
 *
 * General functionality:
 * - connect to the socket server
 * - values received from the server are stored in `pending`
 * - the returned AsyncIterator yields the values from `pending`
 *
 * Based on: <https://github.com/gmac/schema-stitching-handbook/blob/main/mutations-and-subscriptions/lib/make_remote_subscriber.js>
 *
 * @param url Websocket URL to subscribe to
 */
export const createRemoteSubscriber = <T = Record<string, any>>(url: string): CreateRemoteSubscriber<T> => {
  const client = createClient({ url, webSocketImpl: ws });

  // Return the Remote Subscriber
  return async ({ document, variables }: Payload) => {
    // Local state variables, these will be changed from the Sink and the AsyncIterator below
    // `pending` holds the values received from the socket server
    const pending: ExecutionResult<T>[] = [];
    let deferred: {
      resolve: (done: boolean) => void;
      reject: (err: unknown) => void;
    } | null = null;
    let error: unknown = null;
    let done = false;

    // Convert AST DocumentNode to a string
    const query = print(document);

    // Create a Sink to receive the socket server values
    const sink: Sink<ExecutionResult<T>> = {
      next: (data) => {
        // store the data value
        pending.push(data);
        deferred?.resolve(false);
      },
      error: (err) => {
        error = err;
        deferred?.reject(error);
      },
      complete: () => {
        done = true;
        deferred?.resolve(true);
      }
    };

    // Subscribe to the socket server client
    const dispose = client.subscribe(
      {
        query,
        variables
      },
      sink
    );

    // Create an AsyncIterator that yields the values received from the socket server
    return <AsyncIterableIterator<ExecutionResult<T>>>{
      [Symbol.asyncIterator]() {
        // `this` refers to the AsyncIterableIterator itself
        return this;
      },
      async next() {
        if (done) return { done: true, value: undefined };
        if (error) throw error;
        // Return a value that has already been received
        if (pending.length) return { value: pending.shift() };
        // Wait for the next value
        return (await new Promise<boolean>((resolve, reject) => {
          deferred = { resolve, reject };
        }))
          ? { done: true, value: undefined }
          : { value: pending.shift() }; // return the received value
      },
      async return() {
        dispose();
        return { done: true, value: undefined };
      }
    };
  };
};
