import { AsyncExecutor, ExecutionRequest, ExecutionResult } from '@graphql-tools/utils';
import { MaybeAsyncIterable } from '@graphql-tools/utils/typings/executor';
import { fetch } from 'cross-fetch';
import { getOperationAST, print } from 'graphql';
import { createRemoteSubscriber } from './createRemoteSubscriber';
import wait from './wait';

/**
 * Creates a remote executor that can execute GraphQL documents to an external GraphQL API. This
 * executor supports subscriptions
 *
 * @param url GraphQL API endpoint
 * @param timeout Request timeout in ms (default: 2000)
 */
export const createRemoteExecutor = (url: string, timeout = 2000): AsyncExecutor => {
  return async ({
                  operationName,
                  document,
                  variables,
                  context
                }: ExecutionRequest): Promise<MaybeAsyncIterable<ExecutionResult>> => {
    const ast = getOperationAST(document, operationName);
    if (!ast) {
      throw new Error('Unable to identify operation');
    }

    const isSubscription = ast.operation === 'subscription';
    if (isSubscription) {
      const subscribe = createRemoteSubscriber(url.replace('https://', 'wss://').replace('http://', 'ws://'));
      return await subscribe({ document, variables });
    }

    const controller = new AbortController();
    wait(timeout).then(() => controller.abort());
    const query = print(document);

    try {
      const fetchResult = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ query, variables }),
        signal: controller.signal
      });
      return await fetchResult.json();
    } catch (err: Error | unknown) {
      if (err instanceof Error && err.name === 'AbortError') {
        throw new Error(`Request exceeded timeout of ${timeout}ms`);
      }
      throw err;
    }
  };
};
