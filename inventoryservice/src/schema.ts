// Based on: https://github.com/gmac/schema-stitching-handbook/blob/main/stitching-directives-sdl/services/inventory/schema.js
import { makeExecutableSchema } from '@graphql-tools/schema';
import { stitchingDirectives } from '@graphql-tools/stitching-directives';

const { stitchingDirectivesTypeDefs } = stitchingDirectives();

const typeDefinitions = /* GraphQL */ `
  ${stitchingDirectivesTypeDefs}
  "Stuff sitting in warehouse inventory"
  type Product @key(selectionSet: "{ id }") {
    id: ID!
    "Specifies if this product is currently stocked."
    inStock: Boolean
    "Specifies the estimated shipping cost of this product, in cents."
    shippingEstimate: Int @computed(selectionSet: "{ price weight }")
  }

  scalar _Key

  type Query {
    mostStockedProduct: Product
    _products(keys: [_Key!]!): [Product]! @merge
    _sdl: String!
  }
`;

type Product = {
  id: string;
  unitsInStock: number;
};

const inventories = [
  { id: '1', unitsInStock: 3 },
  { id: '2', unitsInStock: 0 },
  { id: '3', unitsInStock: 5 }
] as Product[];

const resolvers = {
  Product: {
    inStock: (product: Product) => product.unitsInStock > 0,
    shippingEstimate(product: { price: number; weight: number }) {
      return product.weight * 2;
    }
  },
  Query: {
    mostStockedProduct: () =>
      inventories.reduce((acc, i) => (acc!.unitsInStock >= i.unitsInStock ? acc : i), inventories[0]),
    _products: (_root: any, { keys }: any) =>
      keys.map((key: { id: string }) => {
        const inventory = inventories.find((i) => i.id === key.id);
        return inventory ? { ...key, ...inventory } : new Error('Not found');
      }),
    _sdl: () => typeDefinitions
  }
};

export const schema = makeExecutableSchema({
  resolvers: [resolvers],
  typeDefs: [typeDefinitions]
});
