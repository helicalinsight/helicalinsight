import searchAndReplace from "../../components/common/hi-shortcuts/utils/search-and-replace-function";

describe('searchAndReplace', () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <div id="test1">
        This is a test string. It should be replaced.
      </div>
      <div id="test2">
        This is another test string. It should also be replaced.
      </div>
    `;
  });

  afterEach(() => {
    document.body.innerHTML = '';
  });

  it('should replace all occurrences of the search string', () => {
    const search = 'test';
    const replace = 'replaced';
    searchAndReplace(search,replace);
    expect(document.body.innerHTML).toContain('This is a replaced string. It should be replaced.');
    expect(document.body.innerHTML).toContain('This is another replaced string. It should also be replaced.');
  });

  it('should handle empty search and replace strings', () => {
    const emptySearch = '';
    const emptyReplace = '';
    searchAndReplace(emptySearch, emptyReplace);
    expect(document.body.innerHTML).toContain('This is a test string. It should be replaced.');
    expect(document.body.innerHTML).toContain('This is another test string. It should also be replaced.');
  });

  it('should not replace any strings if search string is not found', () => {
    const nonExistentSearch = 'nonexistent';
    const replaceString = 'replacement';
    searchAndReplace(nonExistentSearch, replaceString);
    expect(document.body.innerHTML).toContain('This is a test string. It should be replaced.');
    expect(document.body.innerHTML).toContain('This is another test string. It should also be replaced.');
  });

  it('should replace strings with special characters', () => {
    const specialSearch = 'test string.';
    const specialReplace = 'replaced string!';
    searchAndReplace(specialSearch, specialReplace);
    expect(document.body.innerHTML).toContain('This is a replaced string! It should be replaced.');
    expect(document.body.innerHTML).toContain('This is another replaced string! It should also be replaced.');
  });
});
