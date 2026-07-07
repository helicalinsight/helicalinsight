import { configureStore } from '@reduxjs/toolkit';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { PreviewArea } from '../../../components/hi-canned-reports/preview';
import { hiMockAxios } from '../../../app/mock-axios';
import reducers from '../../../redux';


describe('PreviewArea Component', () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: hiMockAxios
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: {
      cannedReports: {
        past: [],
        present: {
          hcrTabData: {
            activeKey: '1',
            panes: [
              {
                key: '1',
                canvasProperties: {
                  layout: {
                    name: 'A4',
                    orientation: 'portrait'
                  }
                }
              }
            ]
          }
        },
        future: []
      }
    },
  });

  test('renders parsed previewTag when present', () => {
    render(
      <Provider store={store}>
        <PreviewArea
          previewTag="<div>Preview Content</div>"
          isPreviewLoading={false}
        />
      </Provider>
    );

    expect(screen.getByText('Preview Content')).toBeInTheDocument();
  });

  test('renders empty A4 layout when previewTag is empty', () => {
    render(
      <Provider store={store}>
        <PreviewArea
          previewTag=""
          isPreviewLoading={false}
        />
      </Provider>
    );

    const emptyA4 = document.querySelector('.a4');
    expect(emptyA4).toBeInTheDocument();
    expect(emptyA4).toHaveStyle({ width: '595px', height: '842px' });
  });

  test('shows loading spinner when isPreviewLoading is true', () => {
    render(
      <Provider store={store}>
        <PreviewArea
          previewTag=""
          isPreviewLoading={true}
        />
      </Provider>
    );

    const spinner = screen.getByRole('img');
    expect(spinner).toHaveClass('anticon-loading');
  });
});
