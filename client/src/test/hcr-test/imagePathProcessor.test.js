import { getPreviewImage } from "../../components/hi-canned-reports/hcrHelperMethods"

describe('getPreviewImage - imagePath handling', () => {
  test('should return link for external image URL', () => {
    const input = { imagePath: 'https://example.com/sample.png', borders: {}, padding: {} };
    const result = getPreviewImage(input);
    expect(result.link).toBe('https://example.com/sample.png');
    expect(result.dir).toBe('');
    expect(result.file).toBe('');
  });

  test('should split internal image path into dir and file', () => {
    const input = { imagePath: 'folder1/folder2/sample.png', borders: {}, padding: {} };
    const result = getPreviewImage(input);
    expect(result.link).toBe('');
    expect(result.dir).toBe('folder1/folder2');
    expect(result.file).toBe('sample.png');
  });

  test('should handle empty imagePath gracefully', () => {
    const input = { imagePath: '', borders: {}, padding: {} };
    const result = getPreviewImage(input);
    expect(result.link).toBe('');
    expect(result.dir).toBe('');
    expect(result.file).toBe('');
  });
});
