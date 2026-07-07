import { getFilterDataType, previousStateInstance } from "../../../../../components/hi-reports/hi-editing-area/utils/filter-utils"
import { getDefaultDisplayDateFormats } from "../../../../../utils/filter-utils"


describe('getFilterDataType', () => {
  test('returns the correct data type', () => {
    const filter = { dataType: 'string' }
    const result = getFilterDataType(filter)
    expect(result).toBe('string')
  })
})




describe('previousStateInstance', () => {
  let instance;

  beforeEach(() => {
    instance = previousStateInstance();
  });

  afterEach(() => {
    instance.clear();
  });

  test('saves and restores config ID correctly', () => {
    const configId = 'abc123';

    instance.save(configId);
    const restoredId = instance.restore(configId);

    expect(restoredId).toEqual(configId);
  });

  test('clears all saved config IDs', () => {
    const configId1 = 'abc123';
    const configId2 = 'def456';

    instance.save(configId1);
    instance.save(configId2);
    instance.clear();

    expect(instance.getMap().size).toEqual(0);
  });
});


describe('test getSupportedDateFormats', () => {
  it('should return the correct date formats', () => {
    const expectedFormats = [
      { key: 'HH:mm:ss', label: 'HH:mm:ss', example: '05:06:07' },
      { key: 'HH', label: 'HH', example: '05' },
      { key: 'mm', label: 'mm', example: '06' },
      { key: 'ss', label: 'ss', example: '07' },
      { key: 'DD', label: 'DD', example: '05' },
      { key: 'MMM', label: 'MMM', example: 'Aug' },
      { key: '[Q]Q', label: '[Q]Q', example: 'Q3' },
      { key: 'YYYY', label: 'YYYY', example: '2024' },
      { key: 'YYYY-MM-DD', label: 'YYYY-MM-DD', example: '2024-06-05' },
      { key: 'MM/DD/YYYY', label: 'MM/DD/YYYY', example: '06/05/2024' },
      { key: 'DD/MM/YYYY', label: 'DD/MM/YYYY', example: '05/06/2024' },
      { key: 'YYYY/MM/DD', label: 'YYYY/MM/DD', example: '2024/06/05' },
      { key: 'DD-MM-YYYY', label: 'DD-MM-YYYY', example: '05-06-2024' },
      { key: 'DD.MM.YYYY', label: 'DD.MM.YYYY', example: '05.06.2024' },
      { key: 'YYYY.MM.DD', label: 'YYYY.MM.DD', example: '2024.06.05' },
      { key: 'MMM DD, YYYY', label: 'MMM DD, YYYY', example: 'Jun 05, 2024' },
      { key: 'MDD, YYYY', label: 'MMMM DD, YYYY', example: 'June 05, 2024' },
      { key: 'DD MMMMM M YYYY', label: 'DD MMM YYYY', example: '05 Jun 2024' },
      { key: 'DD MMMM YYYY', label: 'DD MMMM YYYY', example: '05 June 2024' },
      { key: 'dddd, MMMM DD, YYYY', label: 'dddd, MMMM DD, YYYY', example: 'Wednesday, June 05, 2024' },
      { key: 'MMM DD', label: 'MMM DD', example: 'Jun 05' },
      { key: 'DD MMM', label: 'DD MMM', example: '05 Jun' },
      { key: 'DD-MM-YYYY HH:mm:ss', label: 'DD-MM-YYYY HH:mm:ss', example: '05-06-2024 14:30:00' },
      { key: 'YYYY-MM-DDTHH:mm:ssZ', label: 'YYYY-MM-DDTHH:mm:ssZ', example: '2024-06-05T14:30:00Z' }
    ];

    const dateFormats = getDefaultDisplayDateFormats();
    expect(dateFormats).toEqual(expectedFormats);
  });
});
