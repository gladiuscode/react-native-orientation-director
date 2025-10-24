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

  it('updates the AppDelegate52.swift with the method implementation having public override when sdk is < 53', async function () {
    const appDelegatePath = path.join(
      __dirname,
      './fixtures/AppDelegate52.swift'
    );
    const appDelegate = await fs.promises.readFile(appDelegatePath, 'utf-8');

    const result = swiftFileUpdater(appDelegate, '52.0.0');
    expect(result).toMatchSnapshot();
  });

  it('updates the AppDelegate53.swift with the method implementation without override when sdk is greater than or equal to 53', async function () {
    const appDelegatePath = path.join(
      __dirname,
      './fixtures/AppDelegate53.swift'
    );
    const appDelegate = await fs.promises.readFile(appDelegatePath, 'utf-8');

    const result = swiftFileUpdater(appDelegate, '53.0.0');
    expect(result).toMatchSnapshot();
  });

  it('updates the AppDelegate53.swift with the method implementation without override when sdk is greater than or equal to 54', async function () {
    const appDelegatePath = path.join(
      __dirname,
      './fixtures/AppDelegate53.swift'
    );
    const appDelegate = await fs.promises.readFile(appDelegatePath, 'utf-8');

    const result = swiftFileUpdater(appDelegate, '54.0.0');
    expect(result).toMatchSnapshot();
  });
});
