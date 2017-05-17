"use strict";
(function(){
    var chart = {
        xyz: "hello"
    };
    var classes = {};
    /* Chart Rendering Function*/
    chart.renderChart = function(chartopts) {

        var _chart = {};

        if (classes[chartopts.type]) {

            _chart = $.extend(true, _chart, classes[chartopts.type]);

            /* When we extend _chart from its corresponding class object, it will have the default 'options' defined in the class.
             The below statement is to override the default options with those given by user */

            _chart.options = $.extend(true, _chart.options, chartopts);
            //_chart.parent = this;

            if (typeof _chart.processdata === 'function') {
                _chart.processdata();
            }
            if (typeof _chart.draw === 'function') {
                _chart.draw();
            }
        }

        return _chart;
    };

    window.chart = chart;


    /* Defining various Charts */

    classes.Chart = {
        options: {},
        processdata: null,
        draw: null,
        transform: null
    };

    classes.Charts = $.extend(true, {}, classes.Chart);
    classes.Charts.typemap = {
        PieChart: 'Pie',
        DonutChart: 'donut',
        BarChart: 'bar',
        LineChart: 'line',
        ScatterChart: 'scatter',
        AreaChart: 'area',
        SplineChart: 'spline',
        AreaSplineChart: 'area-spline',
        StepChart: 'step',
        AreaStepChart: 'area-step',
        GaugeChart: 'gauge'
    };
    classes.Charts.transformable = ["BarChart", "LineChart", "AreaChart", "ScatterChart", "AreaStepChart", "SplineChart", "StepChart", "AreaSplineChart", "GaugeChart"];
    classes.Charts.options = {

        type: 'BarChart',
		padding: {},
        data: {

            jsondata: {},
            categorykey: '',
            valuekeys: [],
            groupkeys: [[]],
            axes: {},
            types: {},
            selection: {}
        },


        placeholder: 'chart',

        BarChart: {zerobased: true},

        //title: 'Chart Title',


        size: {
            height: null,
            width: null
        },

        labels: true,

        empty: {
            abort: false
        },

        tooltip: {
            show: true,
            grouped: false
        },

        legend: {
            show: true
        },
        color: {
            pattern: [  '#01cce3' , '#5366d0' , '#ff1d45' , '#ffb200' , '#01c993' , '#5dffda' , '#ff3c80' , '#677077' , '#d3eeef' , '#9068be' , '#daad86' , '#3fb0ac' , '#fae596' , '#7082d9' , '#c43235' , '#fccdd3' , '#bbc4ef' , '#cdd422' , '#b5a397' , '#f89400']
        },

        axis: {
            rotated: false,
            x: {
                localtime: false,
                padding: {top: 20, bottom: 20},//Set padding for x axis
                //Set label for axis.
                label: {
                    text: '',
                    position: 'inner-right'
                },

                type: '',
                tick: {
                    rotate: 0, //Rotate x axis tick text. Default is false.
                    fit: true //Fit x axis tick. Default is true.
                }

            },
            y: {
                show: true, //Show or hides y axis. Default is true.
                inverted: false,
                label: {
                    padding: {},
                    text: '',
                    position: ''
                }
            },
            y2: {
                show: false, //Show or hides y2 axis. Default is true.
                inverted: false,
                center: 0,
                label: {
                    padding: {top: 20, bottom: 20},
                    text: '',
                    position: ''
                }
            }
        },

        point: {
            show: true,			//Whether to show each point in line. Default is true
            r: 2.5,				//The radius size of each point. Default is 2.5
            select: {r: 5},		//The radius size of each point on selected.
            focus: {
                expand: {r: 5}	//Whether to expand each point on focus. Default is true
            }
        },

        regions: [],

        zoom: {
            enabled: false,
            rescale: true
        },

        subchart: {
            show: false
        },

        interaction: {
            enabled: true
        },
        grid: {

            focus: {
                show: false
            },
            x: {
                show: false
            },
            y: {
                focus: {
                    show: false
                },
                show: false
            }
        },
		onresized: function () {},
		onrendered: function () {}
    };

    classes.Charts.draw = function() {
        //console.log("Charts.draw");
        var _options = this.options;
        var types = {};

        for (var key in _options.data.types) {
            types[key] = this.typemap[_options.data.types[key]];
        }
        var c3chart = c3.generate({

            bindto: _options.placeholder,
            data: {
                json: _options.data.jsondata,
                keys: {
                    x: _options.data.categorykey,
                    value: _options.data.valuekeys
                },
                type: this.typemap[_options.type],
                groups: _options.data.groupkeys,
                axes: _options.data.axes,
                labels: _options.data.labels,
                types: _options.data.types,
                selection: _options.data.selection
                
            },

            labels: _options.labels,
            legend: _options.legend,
            interaction: _options.interaction,
            size: _options.size,
            regions: _options.regions,
            empty: _options.empty,
            padding: _options.padding,
            tooltip: _options.tooltip,
            color: _options.color,
            sort: _options.sort,
            onmouseover: _options.onmouseover,
            onmouseout: _options.onmouseout,
            onclick: _options.onclick,
            onselected: _options.onselected,
            onunselected: _options.onunselected,
            ondragstart: _options.ondragstart,
            ondragend: _options.ondragend,
            axis: _options.axis,
            point: _options.point,
            subchart: _options.subchart,
            zoom: _options.zoom,
            grid: _options.grid,
			onresized: _options.onresized,
			onrendered: _options.onrendered
        });

        this.c3chart = c3chart;
    };

    classes.Charts.flow = function(newdata) {
        var _options = this.options;
        this.c3chart.flow({
            json: newdata,
            keys: {
                x: _options.data.categorykey,
                value: _options.data.valuekeys
            },
            length: 3,
            duration: 1500
        });
    };

    classes.Charts.load = function(newdata) {
        var _options = this.options;
        this.c3chart.load({
            json: newdata,
            keys: {
                x: _options.data.categorykey,
                value: _options.data.valuekeys
            },
            duration: 1500
        });
    };
    classes.Charts.transform = function(transformTo){
        //console.log("classes.Charts.transform ");
        //console.log(transformTo);
        var _options = $.extend(true, {}, this.options);
        var newchart = {};
        if ($.inArray(transformTo, this.transformable) > -1) {
            //console.log("Is transformable")
            this.c3chart.transform(this.typemap[transformTo]);
            newchart = this;
        }
        else if($.inArray(transformTo, classes.C3ArrayCharts.transformable) > -1){
            //console.log("Switching");
            _options.type = transformTo;
            _options.data.serieskey = _options.data.categorykey;
            _options.data.valuekey = _options.data.valuekeys[0];
            newchart = chart.renderChart(_options);
        }
        else{
            console.log("Cannot be transformed");
        }
        return newchart;

    };


    classes.C3ArrayCharts = $.extend(true, {}, classes.Chart);
    classes.C3ArrayCharts.typemap = {
        AreaChart: 'area',
        BarChart: 'bar',
        LineChart: 'line',
        ScatterChart: 'scatter',
        SplineChart: 'spline',
        StepChart: 'step',
        AreaSplineChart: 'area-spline',
        AreaStepChart: 'area-step',
        PieChart: 'pie',
        DonutChart: 'donut',
        BubbleChart: 'bubble'
    };

    classes.C3ArrayCharts.transformable = ["PieChart", "DonutChart","BubbleChart"];

    classes.C3ArrayCharts.options = {
        data: {
            jsondata: {},
			pairs: {},
            serieskey: '',
			serieskeys: [],
            valuekey: ''
        },
        placeholder: 'chart',
        title: 'Chart Title',

        size: {
            height: null,
            width: null
        },
        order: 'asc',
        padding: {
            top: 20,
            right: 50,
            bottom: 20,
            left: 50
        },
        empty: {
            abort: false,
            label: {
                text: 'hoge'
            }
        },

        legend: {
            show: true
        },


        selection: {
            enabled: true,
            grouped: false,
            multiple: true
        },
        color: {
            pattern:[  '#01cce3' , '#5366d0' , '#ff1d45' , '#ffb200' , '#01c993' , '#5dffda' , '#ff3c80' , '#677077' , '#d3eeef' , '#9068be' , '#daad86' , '#3fb0ac' , '#fae596' , '#7082d9' , '#c43235' , '#fccdd3' , '#bbc4ef' , '#cdd422' , '#b5a397' , '#f89400']
        },
		onresized: function () {},
		onrendered: function () {}
    };

    classes.C3ArrayCharts.processdata = function () {
        var _options = this.options;
        var _ArrayData = [];
        //console.log("serieskeys:",_options.data.serieskeys);
        //console.log("valuekey:",_options.data.valuekey);
        //console.log("length:",_options.data.jsondata.length);
        //console.log("type:",this.typemap[_options.type]);
        if (this.typemap[_options.type] == "bubble"){
          for (var _i = 0; _i < _options.data.jsondata.length; _i++) {
              _ArrayData[_i] = [];
              _ArrayData[_i].push(_options.data.jsondata[_i][_options.data.serieskeys[0]]);
              _ArrayData[_i].push(_options.data.jsondata[_i][_options.data.serieskeys[1]]);
              _ArrayData[_i].push(_options.data.jsondata[_i][_options.data.valuekey]);
          }
        }else {
          for (var _i = 0; _i < _options.data.jsondata.length; _i++) {
              _ArrayData[_i] = [];
              _ArrayData[_i].push(_options.data.jsondata[_i][_options.data.serieskey]);
              _ArrayData[_i].push(_options.data.jsondata[_i][_options.data.valuekey[0]]);
          }
        }
        //console.log(_ArrayData);
        this.ArrayData = _ArrayData;
    };

    classes.C3ArrayCharts.draw = function() {
        //console.log("C3ArrayCharts.draw");
        var _options = this.options;

        var c3chart = c3.generate({

            bindto: _options.placeholder,
            data: {
                columns: this.ArrayData,
                pairs: this.ArrayData,
                type: this.typemap[_options.type]

            },

            legend: _options.legend,
            size: _options.size,
            padding: _options.padding,
            color: _options.color,
			onresized: _options.onresized,
			onrendered: _options.onrendered
        });

        this.c3chart = c3chart;
    };

    classes.C3ArrayCharts.transform = function(transformTo) {
        //console.log("classes.C3ArrayCharts.transform ");
        //console.log(transformTo);
        var _options = $.extend(true, {}, this.options);
        var newchart = {};
        if ($.inArray(transformTo, this.transformable) > -1) {
            //console.log("Is transformable")
            this.c3chart.transform(this.typemap[transformTo]);
            newchart = this;
        }
        else if($.inArray(transformTo, classes.Charts.transformable) > -1){
            //console.log("Switching");
            _options.type = transformTo;
            _options.data.categorykey = _options.data.serieskey;
            _options.data.valuekeys = [_options.data.valuekey];
            newchart = chart.renderChart(_options);
        }
        else {
            //console.log("Cannot be transformed");
        }
        return newchart;
    };

    classes.C3ArrayCharts.flow = function(newdata) {
        var _options = this.options;
        this.c3chart.flow({
            json: newdata,
            keys: {
                x: _options.data.categorykey,
                value: _options.data.valuekeys
            },
            length: 3,
            duration: 1500
        });
    };

    /* ----------------Defining PieChart--------------------------*/
    classes.PieChart = $.extend(true, {}, classes.C3ArrayCharts);

    /* ----------------Defining C3DonnutChart-----------------------*/
    classes.DonutChart = $.extend(true, {}, classes.C3ArrayCharts);

  /* ----------------Defining C3BubbleChart-----------------------*/
    classes.BubbleChart = $.extend(true, {}, classes.C3ArrayCharts);

    /* ----------------Defining BarChart--------------------------*/
    classes.BarChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining LineChart-------------------------*/
    classes.LineChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining AreaChart-------------------------*/
    classes.AreaChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining SplineChart-----------------------*/
    classes.SplineChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining StepChart-------------------------*/
    classes.StepChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining AreaSplineChart-------------------*/
    classes.AreaSplineChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining AreaStepChart---------------------*/
    classes.AreaStepChart = $.extend(true, {}, classes.Charts);

    /* ----------------Defining ScatterChart-----------------------*/
    classes.ScatterChart = $.extend(true, {}, classes.Charts);
    /*-----------------Defining GaugeChartChart----------------------*/
    classes.GaugeChart = $.extend(true, {}, classes.Charts);
})();
