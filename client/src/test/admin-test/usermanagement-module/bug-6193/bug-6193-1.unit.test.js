import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIAddUserFormWithDrawer } from "../../../../components/hi-admin/components/hi-userManagement/components";
import { storeData } from "./bug-6193-1-mock-data";
import { ConfigProvider } from "antd";
import '@testing-library/jest-dom';

const crypto = require("crypto");

const App = ({ intial_designer_state }) => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: axios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
     preloadedState:{admin:storeData}, 
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ConfigProvider>
        <HIAddUserFormWithDrawer />
        </ConfigProvider>
      </Provider>
    </DndProvider>
  );
};

describe("User Management Add User Form Drawer Test", () => {
  beforeAll(() => {
    delete window.matchMedia;
    window.matchMedia = (query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: jest.fn(), // deprecated
      removeListener: jest.fn(), // deprecated
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    });
    window.HTMLElement.prototype.scrollBy = jest.fn();
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });
  afterAll(() => {
    global.gc && global.gc()
  })
  
  // it('should display an error message when input is *', async () => {
  //   const { getByLabelText,} = render(<App />,);

  //   const input = getByLabelText('Username');
  //   act(()=>{
  //       fireEvent.change(input, { target: { value: '*' } });
  //   })
    
  //   fireEvent.keyDown(document, { key: "Enter", code: "Enter" });
  //   fireEvent.keyUp(document, { key: "Enter", code: "Enter" });
  //   const errorMsg="Username can only use A-Z, a-z, 0-9, -, _,—, ', &, . and can have spaces"
  //   const validationMessage = await screen.findByText(errorMsg);
  //   expect(validationMessage).toHaveTextContent(errorMsg);
  // });

  // it('should display an error message when input is empty', async () => {
  //   const { getByLabelText,} = render(<App />,);

  //   const input = getByLabelText('Username');
  //   act(()=>{
  //       fireEvent.change(input, { target: { value: '' } });
  //       fireEvent.blur(input);
  //   })
    
  //   fireEvent.keyDown(document, { key: "Enter", code: "Enter" });
  //   fireEvent.keyUp(document, { key: "Enter", code: "Enter" });
  //   const errorMsg="Username can only use A-Z, a-z, 0-9, -, _, ', &, . and can have spaces"
  //   const validationMessage = await screen.findByText(errorMsg);
  //   expect(validationMessage).toHaveTextContent(errorMsg);
  // });

  it('should display an error message when input is greater than 120 characters', async () => {
    const { getByLabelText,} = render(<App />,);

    const input = getByLabelText('Username');
    act(()=>{
        fireEvent.change(input, { target: { value: '123'.repeat(120) } });
    })
    
    fireEvent.keyDown(document, { key: "Enter", code: "Enter" });
    fireEvent.keyUp(document, { key: "Enter", code: "Enter" });
    const errorMsg="Username not more than 120 characters!"
    const validationMessage = await screen.findByText(errorMsg);
    expect(validationMessage).toHaveTextContent(errorMsg);
  });

  it('should not display an error message when input is not empty', async () => {
    const { getByLabelText, queryByText } = render(<App />,);

    const input = getByLabelText('Username');
    act(()=>{
        fireEvent.change(input, { target: { value: 'John Doe' } });
        fireEvent.blur(input);

      })

    const errorMessage = queryByText("Username can only use A-Z, a-z, 0-9, -, _,—, ', &, . and can have spaces");
    expect(errorMessage).toBe(null)
  });
});
