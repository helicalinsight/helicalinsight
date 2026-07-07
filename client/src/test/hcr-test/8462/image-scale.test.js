describe("ImageNode iconSize calculation", () => {
  const calculateIconSize = (width, height, padding = {}) => {
    const padTop = padding.Top || 0;
    const padBottom = padding.Bottom || 0;
    const padLeft = padding.Left || 0;
    const padRight = padding.Right || 0;
    const innerWidth = Math.max(0, width - padLeft - padRight);
    const innerHeight = Math.max(0, height - padTop - padBottom);
    const minSize = 16;
    const maxSize = Math.min(innerWidth, innerHeight) * 0.8;
    const size = Math.min(innerWidth, innerHeight) * 0.6;
    return Math.max(minSize, Math.min(size, maxSize));
  };

  const calculateIconSizeOriginal = (width, height, padding = {}) => {
    const padTop = padding.Top || 0;
    const padBottom = padding.Bottom || 0;
    const padLeft = padding.Left || 0;
    const padRight = padding.Right || 0;
    const innerWidth = Math.max(0, width - padLeft - padRight);
    const innerHeight = Math.max(0, height - padTop - padBottom);
    const minSize = 16;
    const maxSize = Math.min(innerWidth, innerHeight) * 0.8;
    const size = Math.min(innerWidth, innerHeight) * 0.6;
    return Math.max(minSize, Math.min(size, maxSize));
  };

  describe("Maximum size", () => {
    it("it should  maximum size based on inner dimensions", () => {
      expect(calculateIconSize(200, 200, {})).toBe(120);
      expect(calculateIconSize(1000, 1000, {})).toBe(600);
    });

    it("it should ensures  smallest dimension", () => {
      expect(calculateIconSize(200, 100, {})).toBe(60);
      expect(calculateIconSize(50, 200, {})).toBe(30);
    });
  });

  describe("Padding ", () => {
    it("it should handles excessive padding inner dimensions", () => {
      expect(
        calculateIconSize(50, 50, {
          Top: 30,
          Bottom: 30,
          Left: 30,
          Right: 30,
        }),
      ).toBe(16);
      expect(
        calculateIconSize(100, 100, {
          Top: 60,
          Bottom: 60,
          Left: 60,
          Right: 60,
        }),
      ).toBe(16);
    });
  });

  describe("Edge cases", () => {
    it("it shoudle handle zero or negative dimensions", () => {
      expect(calculateIconSize(0, 100, {})).toBe(16);
      expect(calculateIconSize(100, 0, {})).toBe(16);
      expect(calculateIconSize(0, 0, {})).toBe(16);
      expect(calculateIconSize(-10, -10, {})).toBe(16);
    });
    it("it should extremely large dimensions", () => {
      expect(calculateIconSize(10000, 10000, {})).toBe(6000);
      expect(calculateIconSize(5000, 3000, {})).toBe(1800);
    });
  });
});
