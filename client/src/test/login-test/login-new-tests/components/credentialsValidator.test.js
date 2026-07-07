import { credentialsValidator } from "../../../../components/hi-login/hi-loginForm/hi-loginForm";


describe('credentialsValidator', () => {
  test('returns 0 when any credential is missing', () => {
    const credentials = {
      username: 'test.user',
      password: 'password123',
    };
    
    const result = credentialsValidator(credentials);
    
    expect(result).toBe(0);
  });

  test('returns 1 when all credentials are provided', () => {
    const credentials = {
      username: 'test.user',
      password: 'password123',
      organization: 'example.com',
    };
    
    const result = credentialsValidator(credentials);
    
    expect(result).toBe(1);
  });
});
