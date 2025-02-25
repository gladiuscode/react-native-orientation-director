import * as fs from 'node:fs';
import * as path from 'node:path';

import { bridgingHeaderUpdater } from '../src/withRNOrientationBridgingHeader';

describe('withRNOrientationBridgingHeader', function () {
  beforeEach(function () {
    jest.resetAllMocks();
  });

  it('updates the App Bridging-Header with the header import', async function () {
    const bridgingHeaderPath = path.join(
      __dirname,
      './fixtures/Bridging-Header.h'
    );
    const bridgingHeader = await fs.promises.readFile(
      bridgingHeaderPath,
      'utf-8'
    );

    const result = bridgingHeaderUpdater(bridgingHeader);
    expect(result).toMatchSnapshot();
  });
});
