import { createYoga } from 'graphql-yoga';
import { GraphQlEndpoint, SchemaBuilder, SchemaLoader } from './lib/SchemaLoader';
import { createServer } from 'http';
import { createRemoteExecutor } from './lib/createRemoteExecutor';
import { GraphQLSchema } from 'graphql/type';
import { stitchSchemas } from '@graphql-tools/stitch';
import { stitchingDirectives } from '@graphql-tools/stitching-directives';
import { wrapSchema } from '@graphql-tools/wrap';
import { buildSchema } from 'graphql/utilities';
import { RemovePrivateElementsTransform } from './lib/RemovePrivateElementsTransform';

const stitchingDirectivesTransformer = stitchingDirectives().stitchingDirectivesTransformer;

const makeEndpoint = (name: string, endpoint: string): GraphQlEndpoint => {
  return {
    name,
    endpoint,
    executor: createRemoteExecutor(endpoint, 5000)
  };
};

const serverPort = process.env['SERVER_PORT'] || 9000 as number;

const endpoints = [
  makeEndpoint('inventoryservice', `http://${process.env['INVENTORY_SERVICE'] || 'localhost:9001'}/graphql`),
  makeEndpoint('productservice', `http://${process.env['PRODUCT_SERVICE'] || 'localhost:9002'}/graphql`),
  makeEndpoint('reviewservice', `http://${process.env['REVIEW_SERVICE'] || 'localhost:9003'}/graphql`),
  makeEndpoint('userservice', `http://${process.env['USER_SERVICE'] || 'localhost:9004'}/graphql`)
];

const schemaBuilder: SchemaBuilder = {
  buildSchema(endpoints: GraphQlEndpoint[]): GraphQLSchema {
    const subSchemas = endpoints.map(endpoint => {
      console.log(`Building schema for service ${endpoint.name}`)
      return { schema: buildSchema(endpoint.sdl!, { assumeValidSDL: true }), executor: endpoint.executor };
    });

    const stitchedSchema = stitchSchemas({
      subschemaConfigTransforms: [stitchingDirectivesTransformer],
      subschemas: subSchemas
    });

    return wrapSchema({
      schema: stitchedSchema,
      transforms: [new RemovePrivateElementsTransform()]
    });
  }
};

const schemaLoader = new SchemaLoader(endpoints, schemaBuilder);
schemaLoader.enableAutoRefresh(10);

const yoga = createYoga({
  maskedErrors: false,
  graphqlEndpoint: '/graphql',
  graphiql: true,
  schema: () => schemaLoader.activeSchema
});

const server = createServer(yoga);
server.listen(serverPort, () => {
  console.info(`Server is running on http://0.0.0.0:${serverPort}/graphql`);
});

const stopApplication = () => {
  console.info('Stopping application ...');
  schemaLoader.disableAutoRefresh();
  server.close();
};
process.on('SIGINT', stopApplication).on('SIGTERM', stopApplication).on('SIGHUP', stopApplication);
