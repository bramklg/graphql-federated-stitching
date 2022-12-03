import { Transform } from '@graphql-tools/delegate/typings/types';
import { filterSchema, pruneSchema } from '@graphql-tools/utils';
import { GraphQLSchema } from 'graphql/type';

/**
 * A GraphQL transformer that removes internal types, fields etc. from a GraphQL Schema.
 * <p>
 * From: <https://www.the-guild.dev/graphql/tools/docs/schema-wrapping#custom-transforms>
 */
export class RemovePrivateElementsTransform implements Transform {
  public transformSchema(originalWrappingSchema: GraphQLSchema): GraphQLSchema {
    const isPublicName = (name: string | undefined) => !name?.startsWith('_');

    return pruneSchema(
      filterSchema({
        schema: originalWrappingSchema,
        typeFilter: (typeName) => isPublicName(typeName),
        rootFieldFilter: (_operationName, fieldName) => isPublicName(fieldName),
        fieldFilter: (_typeName, fieldName) => isPublicName(fieldName),
        argumentFilter: (_typeName, _fieldName, argName) => isPublicName(argName)
      })
    );
  }
}
