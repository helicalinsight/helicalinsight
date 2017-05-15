(function() {
    var callWithJQuery,
        bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
        hasProp = {}.hasOwnProperty,
        indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

    callWithJQuery = function(CrossTabModule) {
        return CrossTabModule(jQuery);
    };

    callWithJQuery(function($) {

    /*
    Utilities
     */
    var PivotData, addSeparators, aggregatorTemplates, aggregators, crossTabRenderer, locales, naturalSort, numberFormat, renderers, usFmt, usFmtInt;

        addSeparators = function(nStr, thousandsSep, decimalSep) {
          var rgx, x, x1, x2;

            nStr += '';
            x = nStr.split('.');
            x1 = x[0];
            x2 = x.length > 1 ? decimalSep + x[1] : '';
            rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + thousandsSep + '$2');
            }
            return x1 + x2;
        };
        numberFormat = function(opts) {
            var defaults;
            defaults = {
                digitsAfterDecimal: 2,
                scaler: 1,
                thousandsSep: ",",
                decimalSep: ".",
                prefix: "",
                suffix: "",
                showZero: true
            };
            opts = $.extend(defaults, opts);
            return function(x) {
                var result;

                if (isNaN(x) || !isFinite(x)) {
                    return "";
                }
                if (x === 0 && !opts.showZero) {
                    return "";
                }
                result = addSeparators((opts.scaler * x).toFixed(opts.digitsAfterDecimal), opts.thousandsSep, opts.decimalSep);
                return "" + opts.prefix + result + opts.suffix;
            };
        };
        usFmt = numberFormat();
        usFmtInt = numberFormat({
            digitsAfterDecimal: 2
        });
        aggregatorTemplates = {
            AsIs: function(formatter) {
                if (formatter === null) {
                    formatter = usFmtInt;
                }
                return function(_arg) {
                    return function(data, rowKey, colKey) {
                        var _Facts, _i;
                        _Facts = {};
                        _i = 0;
                        while (_i < _arg.length) {
                            _Facts[_arg[_i]] = 0;
                            _i++;
                        }
                        return {
                            push: function(record) {
                                _i = 0;
                                while (_i < _arg.length) {
                                    if (!isNaN(parseFloat(record[_arg[_i]]))) {
                                        _Facts[_arg[_i]] += parseFloat(record[_arg[_i]]);
                                    }
                                    _i++;
                                }
                            },
                            multivalue: function() {
                                return _Facts;
                            },
                            value: function() {
                                var attr;

                                attr = _arg[0];
                                return _Facts[_arg[0]];
                            },
                            format: formatter
                        };
                    };
                };
            }
        };
        aggregators = (function(tpl) {
            return {
                "AsIs": tpl.AsIs(usFmtInt)
            };
        })(aggregatorTemplates);
        renderers = {
            "CrossTab": function(pvtData, opts) {
                return crossTabRenderer(pvtData, opts);
            }
        };
        locales = {
            en: {
                aggregators: aggregators,
                renderers: renderers,
                localeStrings: {
                    renderError: "An error occurred rendering the CrossTab.",
                    computeError: "An error occurred computing the CrossTab.",
                    uiRenderError: "An error occurred rendering the CrossTab.",
                    selectAll: "Select All",
                    selectNone: "Select None",
                    tooMany: "(too many to list)",
                    filterResults: "Filter results",
                    totals: "Totals"
                }
            }
        };
        naturalSort = (function(_this) {
            return function(as, bs) {
                var a, a1, b, b1, rd, rx, rz;
                rx = /(\d+)|(\D+)/g;
                rd = /\d/;
                rz = /^0/;
                if (typeof as === "number" || typeof bs === "number") {
                    if (isNaN(as)) {
                        return 1;
                    }
                    if (isNaN(bs)) {
                        return -1;
                    }
                    return as - bs;
                }
                a = String(as).toLowerCase();
                b = String(bs).toLowerCase();
                if (a === b) {
                    return 0;
                }
                if (!(rd.test(a) && rd.test(b))) {
                    return (a > b ? 1 : -1);
                }
                a = a.match(rx);
                b = b.match(rx);
                while (a.length && b.length) {
                    a1 = a.shift();
                    b1 = b.shift();
                    if (a1 !== b1) {
                        if (rd.test(a1) && rd.test(b1)) {
                            return a1.replace(rz, ".0") - b1.replace(rz, ".0");
                        } else {
                            return (a1 > b1 ? 1 : -1);
                        }
                    }
                }
                return a.length - b.length;
            };
        })(this);
        $.pivotUtilities = {
            aggregatorTemplates: aggregatorTemplates,
            aggregators: aggregators,
            renderers: renderers,
            locales: locales,
            naturalSort: naturalSort,
            numberFormat: numberFormat
        };

    /*
    Data Model class
     */
        PivotData = (function() {
            function PivotData(input, opts) {
                this.getAggregator = bind(this.getAggregator, this);
                this.getRowKeys = bind(this.getRowKeys, this);
                this.getColKeys = bind(this.getColKeys, this);
                this.aggregator = opts.aggregator;
                this.aggregatorName = opts.aggregatorName;
                this.colAttrs = opts.cols;
                this.rowAttrs = opts.rows;
                this.valAttrs = opts.vals;
                this.tree = {};
                this.rowKeys = [];
                this.colKeys = [];
                this.rowTotals = {};
                this.colTotals = {};
                this.allTotal = this.aggregator(this, [], []);
                this.sorted = false;
                PivotData.forEachRecord(input, opts.derivedAttributes, (function(_this) {
                    return function(record) {
                        if (opts.filter(record)) {
                            return _this.processRecord(record);
                        }
                    };
                })(this));
            }

            PivotData.forEachRecord = function(input, derivedAttributes, f) {
                var addRecord, compactRecord, i, j, k, l, len1, record, ref, results, results1, tblCols;

                if ($.isEmptyObject(derivedAttributes)) {
                    addRecord = f;
                } else {
                    addRecord = function(record) {
                        var k, ref, v;

                        for (k in derivedAttributes) {
                            v = derivedAttributes[k];
                            record[k] = (ref = v(record)) != null ? ref : record[k];
                        }
                        return f(record);
                    };
                }
                if ($.isFunction(input)) {
                    return input(addRecord);
                } else if ($.isArray(input)) {
                    if ($.isArray(input[0])) {
                        results = [];
                        for (i in input) {
                            if (!hasProp.call(input, i)) continue;
                            compactRecord = input[i];
                            if (!(i > 0)) {
                                continue;
                            }
                            record = {};
                            ref = input[0];
                            for (j in ref) {
                                if (!hasProp.call(ref, j)) continue;
                                k = ref[j];
                                record[k] = compactRecord[j];
                            }
                            results.push(addRecord(record));
                        }
                        return results;
                    } else {
                        results1 = [];
                        for (l = 0, len1 = input.length; l < len1; l++) {
                            record = input[l];
                            results1.push(addRecord(record));
                        }
                        return results1;
                    }
                } else if (input instanceof jQuery) {
                    tblCols = [];
                    $("thead > tr > th", input).each(function(i) {
                        return tblCols.push($(this).text());
                    });
                    return $("tbody > tr", input).each(function(i) {
                        record = {};
                        $("td", this).each(function(j) {
                            return record[tblCols[j]] = $(this).html();
                        });
                        return addRecord(record);
                    });
                } else {
                    throw new Error("unknown input format");
                }
            };

            PivotData.convertToArray = function(input) {
                var result;
                result = [];
                PivotData.forEachRecord(input, {}, function(record) {
                    return result.push(record);
                });
                return result;
            };

            PivotData.prototype.getColKeys = function() {
                return this.colKeys;
            };

            PivotData.prototype.getRowKeys = function() {
                return this.rowKeys;
            };

      PivotData.prototype.processRecord = function(record) {
        var colKey, flatColKey, flatRowKey, l, len1, len2, m, ref, ref1, ref2, ref3, rowKey, x;
        colKey = [];
        rowKey = [];
        ref = this.rowAttrs;
        for (l = 0, len1 = ref.length; l < len1; l++) {
          x = ref[l];
          rowKey.push((ref1 = record[x]) != null ? ref1 : "null");
        }
        ref2 = this.colAttrs;
        for (m = 0, len2 = ref2.length; m < len2; m++) {
          x = ref2[m];
          colKey.push((ref3 = record[x]) != null ? ref3 : "null");
        }
        flatRowKey = rowKey.join(String.fromCharCode(0));
        flatColKey = colKey.join(String.fromCharCode(0));
        this.allTotal.push(record);
        if (rowKey.length !== 0) {
          if (!this.rowTotals[flatRowKey]) {
            this.rowKeys.push(rowKey);
            this.rowTotals[flatRowKey] = this.aggregator(this, rowKey, []);
          }
          this.rowTotals[flatRowKey].push(record);
        }
        if (colKey.length !== 0) {
          if (!this.colTotals[flatColKey]) {
            this.colKeys.push(colKey);
            this.colTotals[flatColKey] = this.aggregator(this, [], colKey);
          }
          this.colTotals[flatColKey].push(record);
        }
        if (colKey.length !== 0 && rowKey.length !== 0) {
          if (!this.tree[flatRowKey]) {
            this.tree[flatRowKey] = {};
          }
          if (!this.tree[flatRowKey][flatColKey]) {
            this.tree[flatRowKey][flatColKey] = this.aggregator(this, rowKey, colKey);
          }
          return this.tree[flatRowKey][flatColKey].push(record);
        }
      };

      PivotData.prototype.getAggregator = function(rowKey, colKey) {
        var agg, flatColKey, flatRowKey;
        flatRowKey = rowKey.join(String.fromCharCode(0));
        flatColKey = colKey.join(String.fromCharCode(0));
        if (rowKey.length === 0 && colKey.length === 0) {
          agg = this.allTotal;
        } else if (rowKey.length === 0) {
          agg = this.colTotals[flatColKey];
        } else if (colKey.length === 0) {
          agg = this.rowTotals[flatRowKey];
        } else {
          agg = this.tree[flatRowKey][flatColKey];
        }
        return agg != null ? agg : {
          value: (function() {
            return null;
          }),
          format: function() {
            return "";
          }
        };
      };

      return PivotData;

    })();

    /*
    Default Renderer for hierarchical table layout
     */
    crossTabRenderer = function(pivotData, opts) {
      var _d, aggregator, c, cl, colAttrs, colKey, colKeys, col_colspan, col_rowspan, cols_length, colspan, html, i, j, r, result, rowAttrs, rowKey, rowKeys, rowspan, spanSize, th, tmpAggregator, totalAggregator, tr, txt, v, val, valAttrs, x;
      aggregator = void 0;
      c = void 0;
      colAttrs = void 0;
      colKey = void 0;
      colKeys = void 0;
      i = void 0;
      j = void 0;
      r = void 0;
      result = void 0;
      rowAttrs = void 0;
      rowKey = void 0;
      rowKeys = void 0;
      th = void 0;
      totalAggregator = void 0;
      tr = void 0;
      txt = void 0;
      val = void 0;
      x = void 0;
      valAttrs = pivotData.valAttrs;
      if (pivotData.colAttrs.length !== 0) {
        colAttrs = pivotData.colAttrs;
      } else {
        colAttrs = [];
      }
      if (pivotData.rowAttrs.length !== 0) {
        rowAttrs = pivotData.rowAttrs;
      } else {
        rowAttrs = [];
      }
      if (pivotData.getRowKeys().length !== 0) {
        rowKeys = pivotData.getRowKeys();
      } else {
        rowKeys = [];
      }
      if (pivotData.getColKeys().lenght !== 0) {
        colKeys = pivotData.getColKeys();
      } else {
        colKeys = [];
      }
      spanSize = function(arr, i, j) {
        var _i, _j, len, noDraw, stop;
        len = void 0;
        noDraw = void 0;
        stop = void 0;
        x = void 0;
        _i = void 0;
        _j = void 0;
        if (i !== 0) {
          noDraw = true;
          x = _i = 0;
          while ((0 <= j ? _i <= j : _i >= j)) {
            if (arr[i - 1][x] !== arr[i][x]) {
              noDraw = false;
            }
            x = (0 <= j ? ++_i : --_i);
          }
          if (noDraw) {
            return -1;
          }
        }
        len = 0;
        while (i + len < arr.length) {
          stop = false;
          x = _j = 0;
          while ((0 <= j ? _j <= j : _j >= j)) {
            if (arr[i][x] !== arr[i + len][x]) {
              stop = true;
            }
            x = (0 <= j ? ++_j : --_j);
          }
          if (stop) {
            break;
          }
          len++;
        }
        return len;
      };
      result = $("<table class='table table-bordered table-striped'>");
      for (j in colAttrs) {
        if (!hasProp.call(colAttrs, j)) continue;
        c = colAttrs[j];
        tr = $("<tr>");
        if (parseInt(j) === 0 && rowAttrs.length !== 0) {
          tr.append($("<th>").attr("colspan", rowAttrs.length).attr("rowspan", colAttrs.length));
        }
        tr.append($("<th class='hi-pvtAxisLabel'>").text(c));
        tmpAggregator = pivotData.getAggregator([], []);
        if (tmpAggregator.multivalue) {
          col_colspan = Object.keys(tmpAggregator.multivalue()).length;
          col_rowspan = 1;
        } else {
          col_colspan = 1;
          col_rowspan = 2;
        }
        for (i in colKeys) {
          if (!hasProp.call(colKeys, i)) continue;
          colKey = colKeys[i];
          th = $("<th class='hi-pvtColLabel'>").text(colKey[j]).attr("colspan", col_colspan);
          if (parseInt(j) === colAttrs.length - 1 && rowAttrs.length !== 0) {
            th.attr("rowspan", col_rowspan);
          }
          tr.append(th);
        }
        result.append(tr);
      }
      if (rowAttrs.length !== 0) {
        tr = $("<tr>");
        for (i in rowAttrs) {
          if (!hasProp.call(rowAttrs, i)) continue;
          r = rowAttrs[i];
          tr.append($("<th class='hi-pvtAxisLabel'>").text(r));
        }
        tmpAggregator = pivotData.getAggregator([], []);
        if (tmpAggregator.multivalue) {
          if (colAttrs.length > 0) {
            th = $("<th>");
            tr.append(th);
          }
          val = tmpAggregator.multivalue();
          for (i in colKeys) {
            if (!hasProp.call(colKeys, i)) continue;
            for (v in val) {
              if (!hasProp.call(val, v)) continue;
              tr.append($("<th class='hi-pvtColLabel'>").text(v).data("value", v));
            }
          }
          if(colKeys.length === 0){
                for (v in val) {
                        if (!hasProp.call(val, v)) continue;
                        tr.append($("<th class='hi-pvtColLabel'>").text(v).data("value", v));
                }
          }
        }
        result.append(tr);
      }
      for (i in rowKeys) {
        if (!hasProp.call(rowKeys, i)) continue;
        rowKey = rowKeys[i];
        tr = $("<tr>");
        for (j in rowKey) {
          if (!hasProp.call(rowKey, j)) continue;
          txt = rowKey[j];
          if (parseInt(j) === rowAttrs.length - 1 && colAttrs.length !== 0) {
            colspan = 2;
          } else {
            colspan = 1;
          }
          rowspan = spanSize(rowKeys, parseInt(i), parseInt(j));
          if (rowspan !== -1) {
            th = $("<th class='hi-pvtRowLabel'>").text(txt).attr("rowspan", rowspan).attr("colspan", colspan).css("vertical-align", "middle");
            tr.append(th);
          }
        }
          for (j in colKeys) {
              if (!hasProp.call(colKeys, j)) continue;
              colKey = colKeys[j];
              aggregator = pivotData.getAggregator(rowKey, colKey);
              if (aggregator.multivalue) {
                  val = aggregator.multivalue();
                  for (v in val) {
                      if (!hasProp.call(val, v)) continue;
                      tr.append($("<td class='hi-pvtVal row" + i + " col" + j + "-" + v + "'>").text(aggregator.format(val[v])).data("value", val[v]));
                  }
              } else {
                  val = aggregator.value();
                  if (val) {
                      tr.append($("<td class='hi-pvtVal row" + i + " col" + j + "'>").text(aggregator.format(val)).data("value", val));
                  } else {
                      tmpAggregator = pivotData.getAggregator([], []);
                      cols_length = 1;
                      if (tmpAggregator.multivalue) {
                          cols_length = Object.keys(tmpAggregator.multivalue()).length;
                      }
                      cl = 0;
                      while (cl < cols_length) {
                          tr.append($("<td class='hi-pvtVal row" + i + " col" + j + "-" + cl + "'>").text("").data("value", null));
                          cl++;
                      }
                  }
              }
          }
          if(colKeys.length === 0){
                  totalAggregator = pivotData.getAggregator(rowKey, []);
                    if (totalAggregator.multivalue) {
                        val = totalAggregator.multivalue();
                        for (v in val) {
                            tr.append("<td class='hi-pvtTotal'>" + totalAggregator.format(val[v]) + "</td>");
                        }
                    } else {
                        val = totalAggregator.value();
                        tr.append($("<td class='hi-pvtTotal'>").text(totalAggregator.format(val)).data("value", val).data("for", "row" + i));
                    }
          }
          result.append(tr);
      }
        result.append(tr);
        result.data("dimensions", [rowKeys.length, colKeys.length]);
        return result;
    };

    /*
    Pivot Table core: create PivotData object and call Renderer on it
     */
    $.fn.pivot = function(input, opts) {
      var defaults, e, error, error1, pivotData, result, x;
      defaults = {
        cols: [],
        rows: [],
        filter: function() {
          return true;
        },
        aggregator: aggregatorTemplates.AsIs()(),
        aggregatorName: "AsIs",
        derivedAttributes: {},
        renderer: crossTabRenderer,
        rendererOptions: null,
        localeStrings: locales.en.localeStrings
      };
      opts = $.extend(defaults, opts);
      result = null;
      try {
        pivotData = new PivotData(input, opts);
        try {
          result = opts.renderer(pivotData, opts.rendererOptions);
        } catch (error) {
          e = error;
          if (typeof console !== "undefined" && console !== null) {
            console.error(e);
          }
          result = $("<span>").html(opts.localeStrings.renderError);
        }
      } catch (error1) {
        e = error1;
        if (typeof console !== "undefined" && console !== null) {
          console.error(e);
        }
        result = $("<span>").html(opts.localeStrings.computeError);
      }
      x = this[0];
      while (x.hasChildNodes()) {
        x.removeChild(x.lastChild);
      }
      return this.append(result);
    };

    /*
    Pivot Table UI: calls Pivot Table core above with options set by user
     */
    $.fn.Crosstab = function(input, inputOpts, overwrite, locale) {
      var CrossTable, aggregator, axisValues, c, colList, defaults, e, error, existingOpts, fn, i, initialRender, k, l, len1, len2, len3, m, n, opts, ref, ref1, ref2, ref3, ref4, refresh, refreshDelayed, renderer, rendererControl, shownAttributes, tblCols, tr1, tr2, uiTable, x;
      if (overwrite == null) {
        overwrite = false;
      }
      if (locale == null) {
        locale = "en";
      }
      defaults = {
        derivedAttributes: {},
        aggregators: locales[locale].aggregators,
        renderers: locales[locale].renderers,
        menuLimit: 2000,
        cols: [],
        rows: [],
        vals: [],
        rendererOptions: {
          localeStrings: locales[locale].localeStrings
        },
        onRefresh: null,
        filter: function() {
          return true;
        },
        localeStrings: locales[locale].localeStrings
      };
      existingOpts = this.data("CrosstabOptions");
      if ((existingOpts == null) || overwrite) {
        opts = $.extend(defaults, inputOpts);
      } else {
        opts = existingOpts;
      }
      try {
        input = PivotData.convertToArray(input);
        tblCols = (function() {
          var ref, results;
          ref = input[0];
          results = [];
          for (k in ref) {
            if (!hasProp.call(ref, k)) continue;
            results.push(k);
          }
          return results;
        })();
        ref = opts.derivedAttributes;
        for (c in ref) {
          if (!hasProp.call(ref, c)) continue;
          if ((indexOf.call(tblCols, c) < 0)) {
            tblCols.push(c);
          }
        }
        axisValues = {};
        for (l = 0, len1 = tblCols.length; l < len1; l++) {
          x = tblCols[l];
          axisValues[x] = {};
        }
        PivotData.forEachRecord(input, opts.derivedAttributes, function(record) {
          var base, results, v;
          results = [];
          for (k in record) {
            if (!hasProp.call(record, k)) continue;
            v = record[k];
            if (!(opts.filter(record))) {
              continue;
            }
            if (v == null) {
              v = "null";
            }
            if ((base = axisValues[k])[v] == null) {
              base[v] = 0;
            }
            results.push(axisValues[k][v]++);
          }
          return results;
        });
        uiTable = $("<table>").attr("cellpadding", 5);
        rendererControl = $("<td>");
        renderer = $("<select>").addClass('pvtRenderer hi-hide').appendTo(rendererControl).bind("change", function() {
          return refresh();
        });
        ref1 = opts.renderers;
        for (x in ref1) {
          if (!hasProp.call(ref1, x)) continue;
          $("<option>").val(x).html(x).appendTo(renderer);
        }
        colList = $("<td>").addClass('hi-pvtAxisContainer pvtUnused hi-hide');
        shownAttributes = (function() {
          var len2, m, results;
          results = [];
          for (m = 0, len2 = tblCols.length; m < len2; m++) {
            c = tblCols[m];
            results.push(c);
          }
          return results;
        })();
        colList.addClass('hi-pvtHorizList');
        fn = function(c) {
          var attrElem, keys, valueList;
          keys = (function() {
            var results;
            results = [];
            for (k in axisValues[c]) {
              results.push(k);
            }
            return results;
          })();
          valueList = $("<div>").addClass('pvtFilterBox').hide();
          attrElem = $("<li>").addClass("axis_" + i).append($("<span>").addClass('pvtAttr').text(c).data("attrName", c));
          return colList.append(attrElem);
        };
        for (i in shownAttributes) {
          c = shownAttributes[i];
          fn(c);
        }
        tr1 = $("<tr>").appendTo(uiTable);
        aggregator = $("<select>").addClass('hi-pvtAggregator').bind("change", function() {
          return refresh();
        });
        ref2 = opts.aggregators;
        for (x in ref2) {
          if (!hasProp.call(ref2, x)) continue;
          aggregator.append($("<option>").val(x).html(x));
        }
        $("<td>").addClass('hi-pvtVals hi-hide').appendTo(tr1).append(aggregator).append($("<br>"));
        $("<td>").addClass('hi-pvtAxisContainer hi-pvtHorizList pvtCols hi-hide').appendTo(tr1);
        tr2 = $("<tr>").appendTo(uiTable);
        tr2.append($("<td>").addClass('hi-pvtAxisContainer pvtRows hi-hide').attr("valign", "top"));
        CrossTable = $("<td>").attr("valign", "top").addClass('pvtRendererArea').appendTo(tr2);
        uiTable.prepend($("<tr>").append(rendererControl).append(colList));
        this.html(uiTable);
        ref3 = opts.cols;
        for (m = 0, len2 = ref3.length; m < len2; m++) {
          x = ref3[m];
          this.find(".pvtCols").append(this.find(".axis_" + ($.inArray(x, shownAttributes))));
        }
        ref4 = opts.rows;
        for (n = 0, len3 = ref4.length; n < len3; n++) {
          x = ref4[n];
          this.find(".pvtRows").append(this.find(".axis_" + ($.inArray(x, shownAttributes))));
        }
        if (opts.aggregatorName != null) {
          this.find(".hi-pvtAggregator").val(opts.aggregatorName);
        }
        if (opts.rendererName != null) {
          this.find(".pvtRenderer hi-hide").val(opts.rendererName);
        }
        initialRender = true;
        refreshDelayed = (function(_this) {
          return function() {
            var CrosstabOptions, attr, len4, newDropdown, numInputsToProcess, o, p, pvtVals, ref5, ref6, subopts, vals;
            subopts = {
              derivedAttributes: opts.derivedAttributes,
              localeStrings: opts.localeStrings,
              rendererOptions: opts.rendererOptions,
              cols: [],
              rows: []
            };
            numInputsToProcess = (ref5 = opts.aggregators[aggregator.val()]([])().numInputs) != null ? ref5 : 0;
            vals = [];
            _this.find(".pvtRows li span.pvtAttr").each(function() {
              return subopts.rows.push($(this).data("attrName"));
            });
            _this.find(".pvtCols li span.pvtAttr").each(function() {
              return subopts.cols.push($(this).data("attrName"));
            });
            _this.find(".hi-pvtVals select.pvtAttrDropdown hi-hide").each(function() {
              if (numInputsToProcess === 0) {
                return $(this).remove();
              } else {
                numInputsToProcess--;
                if ($(this).val() !== "") {
                  return vals.push($(this).val());
                }
              }
            });
            if (numInputsToProcess !== 0) {
              pvtVals = _this.find(".hi-pvtVals");
              for (x = o = 0, ref6 = numInputsToProcess; 0 <= ref6 ? o < ref6 : o > ref6; x = 0 <= ref6 ? ++o : --o) {
                newDropdown = $("<select>").addClass('pvtAttrDropdown').append($("<option>")).bind("change", function() {
                  return refresh();
                });
                for (p = 0, len4 = shownAttributes.length; p < len4; p++) {
                  attr = shownAttributes[p];
                  newDropdown.append($("<option>").val(attr).text(attr));
                }
                pvtVals.append(newDropdown);
              }
            }
            if ((opts.aggregatorName != null) && opts.aggregatorName === "AsIs") {
              vals = opts.vals;
            }
            if (initialRender) {
              i = 0;
              _this.find(".hi-pvtVals select.pvtAttrDropdown").each(function() {
                $(this).val(vals[i]);
                return i++;
              });
              initialRender = false;
            }
            subopts.aggregatorName = aggregator.val();
            subopts.vals = vals;
            subopts.aggregator = opts.aggregators[aggregator.val()](vals);
            subopts.renderer = opts.renderers[renderer.val()];
            CrossTable.pivot(input, subopts);
            CrosstabOptions = $.extend(opts, {
              cols: subopts.cols,
              rows: subopts.rows,
              vals: vals,
              aggregatorName: aggregator.val(),
              rendererName: renderer.val()
            });
            _this.data("CrosstabOptions", CrosstabOptions);
            CrossTable.css("opacity", 1);
            if (opts.onRefresh != null) {
              return opts.onRefresh(CrosstabOptions);
            }
          };
        })(this);
        refresh = (function(_this) {
          return function() {
            CrossTable.css("opacity", 0.5);
            return setTimeout(refreshDelayed, 10);
          };
        })(this);
        refresh();
      } catch (error) {
        e = error;
        if (typeof console !== "undefined" && console !== null) {
          console.error(e);
        }
        this.html(opts.localeStrings.uiRenderError);
      }
      return this;
    };

    /*
    Heatmap post-processing
     */
    return $.fn.heatmap = function(scope) {
      var colorGen, heatmapper, i, j, l, m, numCols, numRows, ref, ref1;
      if (scope == null) {
        scope = "heatmap";
      }
      numRows = this.data("numrows");
      numCols = this.data("numcols");
      colorGen = function(color, min, max) {
        var hexGen;
        hexGen = (function() {
          switch (color) {
            case "red":
              return function(hex) {
                return "ff" + hex + hex;
              };
            case "green":
              return function(hex) {
                return hex + "ff" + hex;
              };
            case "blue":
              return function(hex) {
                return "" + hex + hex + "ff";
              };
          }
        })();
        return function(x) {
          var hex, intensity;
          intensity = 255 - Math.round(255 * (x - min) / (max - min));
          hex = intensity.toString(16).split(".")[0];
          if (hex.length === 1) {
            hex = 0 + hex;
          }
          return hexGen(hex);
        };
      };
      heatmapper = (function(_this) {
        return function(scope, color) {
          var colorFor, forEachCell, values;
          forEachCell = function(f) {
            return _this.find(scope).each(function() {
              var x;
              x = $(this).data("value");
              if ((x != null) && isFinite(x)) {
                return f(x, $(this));
              }
            });
          };
          values = [];
          forEachCell(function(x) {
            return values.push(x);
          });
          colorFor = colorGen(color, Math.min.apply(Math, values), Math.max.apply(Math, values));
          return forEachCell(function(x, elem) {
            return elem.css("background-color", "#" + colorFor(x));
          });
        };
      })(this);
      switch (scope) {
        case "heatmap":
          heatmapper(".hi-pvtVal", "red");
          break;
        case "rowheatmap":
          for (i = l = 0, ref = numRows; 0 <= ref ? l < ref : l > ref; i = 0 <= ref ? ++l : --l) {
            heatmapper(".pvtVal.row" + i, "red");
          }
          break;
        case "colheatmap":
          for (j = m = 0, ref1 = numCols; 0 <= ref1 ? m < ref1 : m > ref1; j = 0 <= ref1 ? ++m : --m) {
            heatmapper(".hi-pvtVal.col" + j, "red");
          }
      }
      heatmapper(".hi-pvtTotal.rowTotal", "red");
      heatmapper(".hi-pvtTotal.colTotal", "red");
      return this;
    };
  });

}).call(this);
