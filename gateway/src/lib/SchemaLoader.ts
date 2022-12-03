import { AsyncExecutor } from '@graphql-tools/utils';
import { parse } from 'graphql';
import { GraphQLSchema } from 'graphql/type';
import wait from './wait';

export type GraphQlEndpoint = {
  name: string;
  endpoint: string;
  executor: AsyncExecutor;
  sdl?: string;
};

export interface SchemaBuilder {
  /**
   * Build a stitched `GraphQLSchema` super schema of all loaded endpoints
   *
   * @param endpoints All loaded endpoints. SDL is never undefined here
   */
  buildSchema(endpoints: GraphQlEndpoint[]): GraphQLSchema
}

/**
 * `SchemaLoader` that users the GraphQL query `{ _sdl }` to fetch the raw GraphQL SDL of a service
 * The service can be used to poll for new schema's on a set interval. Schema introspection is not supported
 */
export class SchemaLoader {
  private activeEndpointData: Map<string, GraphQlEndpoint> = new Map();
  private sdlCache: Map<string, string> = new Map();
  private autoRefreshEnabled = false;

  /**
   * The active schema. By default, this is an empty schema,
   * this will be populated after one or more schemas are loaded */
  public activeSchema: GraphQLSchema = new GraphQLSchema({});

  /**
   * @param endpoints All endpoints that expose the GraphQL SDL through the `{ _sdl }` query
   * @param schemaBuilder Schema builder to build the super schema based on fetched sub schema's
   */
  constructor(private endpoints: GraphQlEndpoint[], private schemaBuilder: SchemaBuilder) {
  }

  /**
   * Reloads all schema's and installs a new `activeSchema` if there are any changes detected
   */
  public async reload(): Promise<void> {
    const remoteSchemaData = await this.fetchRemoteSchemas();
    const loadedEndpoints: Map<string, GraphQlEndpoint> = new Map(
      remoteSchemaData.filter((loadedEndpoint) => !!loadedEndpoint.sdl).map((value) => [value.name, value])
    );

    const dataEqual =
      this.activeEndpointData.size === loadedEndpoints.size &&
      Array.from(this.activeEndpointData.keys()).every(
        (key) => this.activeEndpointData.get(key)?.sdl === loadedEndpoints.get(key)?.sdl
      );

    if (dataEqual) {
      console.debug('Reloaded schema data was identical to active schema data, not rebuilding schema');
    } else {
      console.info('Schema data changed, building new super schema');
      try {
        this.activeSchema = this.schemaBuilder.buildSchema(Array.from(loadedEndpoints.values()));
        this.activeEndpointData = loadedEndpoints;
      } catch (error) {
        console.error(error);
      }
    }
  }

  /**
   * Enables auto refreshing of the (remote) schema's on the set interval
   *
   * @param intervalSecs Poll interval in seconds. Default: 5
   */
  public enableAutoRefresh(intervalSecs = 5): void {
    this.autoRefreshEnabled = true;
    this.autoRefresh(intervalSecs);
  }

  /**
   * Disables auto refresh (if it was enabled). no-op if auto refresh is not enabled
   */
  public disableAutoRefresh(): void {
    this.autoRefreshEnabled = false;
  }

  /**
   * Do the actual auto refresh
   * @param intervalSecs Poll interval in seconds
   */
  private async autoRefresh(intervalSecs: number) {
    if (this.autoRefreshEnabled) {
      await this.reload();
      await wait(intervalSecs * 1000);
      await this.autoRefresh(intervalSecs);
    }
  }

  /**
   * Fetches the remote schema(s) by performing the GraphQL Query `{ _sdl }`. This schema loader
   * does *not* support schema loading by introspection
   */
  private async fetchRemoteSchemas(): Promise<GraphQlEndpoint[]> {
    const promises = this.endpoints.map(async (endpoint) => {
      endpoint.sdl = this.sdlCache.get(endpoint.name);

      try {
        console.debug(`Requesting SDL schema for ${endpoint.name} at ${endpoint.endpoint}`);

        // Returns a GraphQL JSON response containing the result of the SDL query
        const graphQlResponse = (await endpoint.executor({ document: parse('{ _sdl }') })) as any;
        const { data } = graphQlResponse;
        endpoint.sdl = data._sdl;

        if (endpoint.sdl) {
          console.debug(`Fetched SDL schema for ${endpoint.name}`);
          this.sdlCache.set(endpoint.name, endpoint.sdl);
        }
      } catch (error) {
        console.error(error);
      }

      return endpoint;
    });

    return await Promise.all(promises);
  }
}

