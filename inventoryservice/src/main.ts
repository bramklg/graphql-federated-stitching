import { createYoga } from 'graphql-yoga';
import { createServer } from 'http';
import { schema } from './schema';

const serverPort = process.env['SERVER_PORT'] || 9001;

const yoga = createYoga({ schema });
const server = createServer(yoga);

server.listen(serverPort, () => {
  console.info(`Server is running on http://localhost:${serverPort}/graphql`);
});

const stopApplication = () => {
  console.info('Stopping application ...');
  server.close();
};
process.on('SIGINT', stopApplication).on('SIGTERM', stopApplication).on('SIGHUP', stopApplication);
