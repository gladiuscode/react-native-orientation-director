import * as fs from 'node:fs';
import * as path from 'node:path';

import { ktFileUpdater } from '../src/withRNOrientationMainActivity';

describe('withRNOrientationMainActivity', function () {
  beforeEach(function () {
    jest.resetAllMocks();
  });

  it('updates the MainActivity.kt with both imports and method implementation', async function () {
    const mainActivityPath = path.join(__dirname, './fixtures/MainActivity.kt');
    const mainActivity = await fs.promises.readFile(mainActivityPath, 'utf-8');

    const result = ktFileUpdater(mainActivity);
    expect(result).toMatchSnapshot();
  });

  it("skips the MainActivity.kt intent import when it's already set", async function () {
    const mainActivityPath = path.join(
      __dirname,
      './fixtures/MainActivityWithIntentImport.kt'
    );
    const mainActivity = await fs.promises.readFile(mainActivityPath, 'utf-8');

    const result = ktFileUpdater(mainActivity);
    expect(result).toMatchSnapshot();
  });

  it("skips the MainActivity.kt configuration import when it's already set", async function () {
    const mainActivityPath = path.join(
      __dirname,
      './fixtures/MainActivityWithConfigurationImport.kt'
    );
    const mainActivity = await fs.promises.readFile(mainActivityPath, 'utf-8');

    const result = ktFileUpdater(mainActivity);
    expect(result).toMatchSnapshot();
  });
});
