const interpolate = /\{\{(.+?)\}\}/g;

export const decompile = (tpl, str, result = {}) => {
  let regex = new RegExp(interpolate.source),
    match = tpl.match(regex);

  if (!match) {
    return result;
  }

  let prop = match[1].replace(/ /g, ""),
    templateSplit = tpl.split(match[0]),
    secondSplitPivot = templateSplit[1].split(regex)[0],
    value = str.replace(templateSplit[0], "");

  if (secondSplitPivot) {
    value = value.split(secondSplitPivot)[0];
  }
  result[prop] = value;
  let encodedValue = encodeURIComponent(value);
  let updatedTpl = tpl.replace(regex, encodedValue);
  updatedTpl = decodeURIComponent(updatedTpl)
  return decompile(updatedTpl, str, result);
};
