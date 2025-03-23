import * as fs from 'node:fs';
import * as path from 'node:path';

import { ktFileUpdater } from '../src/withRNOrientationMainActivity';

describe('withRNOrientationMainActivity', function () {
  beforeEach(function () {
    jest.resetAllMocks();
  });

  it('updates the MainActivity.kt with both import and method implementation', async function () {
    const mainActivityPath = path.join(__dirname, './fixtures/MainActivity.kt');
    const mainActivity = await fs.promises.readFile(mainActivityPath, 'utf-8');

    const result = ktFileUpdater(mainActivity);
    expect(result).toMatchSnapshot();
  });

  it('updates the MainActivity.java with both the import the method implementation', async function () {
    // TODO: Implement java test
  });
});
