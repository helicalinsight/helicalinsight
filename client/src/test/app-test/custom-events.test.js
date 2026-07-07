import { publish } from "../../app/custom-events";

describe('publish', () => {
  let dispatchSpy;

  beforeEach(() => {
    dispatchSpy = jest.spyOn(window, 'dispatchEvent');
  });

  afterEach(() => {
    dispatchSpy.mockRestore();
  });

  it('should dispatch a CustomEvent with the specified name and data', () => {
    const eventName = 'hiReadyState';
    const eventData = { changeTime: new Date(), status: 'completed', apiCalls: 0 };
    publish(eventName, eventData);

    expect(dispatchSpy).toHaveBeenCalledWith(expect.any(CustomEvent));
    const dispatchedEvent = dispatchSpy.mock.calls[0][0];
    expect(dispatchedEvent.type).toBe(eventName);
    expect(dispatchedEvent.detail).toBe(eventData);
    expect(dispatchedEvent.data).toBe(eventData);
  });

  it('should dispatch a CustomEvent with default data if none is provided', () => {
    const eventName = 'hiReadyState';
    publish(eventName);

    expect(dispatchSpy).toHaveBeenCalledWith(expect.any(CustomEvent));
    const dispatchedEvent = dispatchSpy.mock.calls[0][0];
    expect(dispatchedEvent.type).toBe(eventName);
    expect(dispatchedEvent.detail).toEqual({ changeTime: expect.any(Date), status: 'initial', apiCalls: 0 });
    expect(dispatchedEvent.data).toEqual({ changeTime: expect.any(Date), status: 'initial', apiCalls: 0 });
  });
});

