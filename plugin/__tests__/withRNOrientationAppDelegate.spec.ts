import * as fs from 'node:fs';
import * as path from 'node:path';

import {
  objCFileUpdater,
  swiftFileUpdater,
} from '../src/withRNOrientationAppDelegate';

describe('withRNOrientationAppDelegate', function () {
  beforeEach(function () {
    jest.resetAllMocks();
  });

  it('updates the AppDelegate.mm with both header import and method implementation', async function () {
    const appDelegatePath = path.join(__dirname, './fixtures/AppDelegate.mm');
    const appDelegate = await fs.promises.readFile(appDelegatePath, 'utf-8');

    const result = objCFileUpdater(appDelegate);
    expect(result).toMatchSnapshot();
  });

  it('updates the AppDelegate.swift with the method implementation', async function () {
    const appDelegatePath = path.join(
      __dirname,
      './fixtures/AppDelegate.swift'
    );
    const appDelegate = await fs.promises.readFile(appDelegatePath, 'utf-8');

    const result = swiftFileUpdater(appDelegate);
    expect(result).toMatchSnapshot();
  });
});
