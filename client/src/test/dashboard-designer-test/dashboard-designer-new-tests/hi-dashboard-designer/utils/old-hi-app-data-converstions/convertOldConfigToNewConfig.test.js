import { convertOldConfigToNewConfig } from "../../../../../../components/hi-dashboard-designer/utils/old-hi-app-data-converstions";

describe("convertOldConfigToNewConfig", () => {
  test("should convert old config data to new config format", () => {
    const oldConfig = {
      header: {
        enable: true,
        text: "My header",
        href: "https://example.com",
        color: "#000000",
      },
      shadow: {
        enable: true,
        xoffset: 2,
        yoffset: 2,
        blur: 4,
        spread: 0,
        color: "#333333",
      },
      background: {
        enable: true,
        color: "#ffffff",
        imgUrl: "https://example.com/image.jpg",
      },
      border: {
        enable: true,
        weight: 2,
        borderStyle: "dotted",
        color: "#777777",
      },
      htmlCode: {
        enable: true,
        content: "<h1>Hello, world!</h1>",
      },
      cssCode: {
        enable: true,
        content: "body { font-family: Arial; }",
      },
      jsCode: {
        enable: true,
        content: "console.log('Hello, world!');",
      },
    };
    const result = convertOldConfigToNewConfig(oldConfig);
    expect(result).toEqual([
      {
        key: "header",
        values: {
          enable: true,
          title: "My header",
          placeholder: "Edit/Add your header content here",
          link: "https://example.com",
          backgroundColor: "#000000",
        },
      },
      {
        key: "shadow",
        values: {
          enable: true,
          xOffset: 2,
          yOffset: 2,
          blur: 4,
          spread: 0,
          color: "#333333",
        },
      },
      {
        key: "background",
        values: {
          enable: true,
          backgroundColor: "#ffffff",
          image: "https://example.com/image.jpg",
        },
      },
      {
        key: "border",
        values: {
          enable: true,
          borderWidth: 2,
          borderStyle: "dotted",
          color: "#777777",
        },
      },
      {
        key: "html",
        values: { enable: true, value: "<h1>Hello, world!</h1>" },
      },
      {
        key: "css",
        values: { enable: true, value: "body { font-family: Arial; }" },
      },
      {
        key: "javascript",
        values: { enable: true, value: "console.log('Hello, world!');" },
      },
    ]);
  });
});
