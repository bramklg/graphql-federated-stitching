/**
 * @param delayInMs Amount of milliseconds to wait
 */
export default async (delayInMs: number) => new Promise((resolve) => setTimeout(resolve, delayInMs));
