define('CVC', ['g2','renderer'], function (G2) {
  return function (instanceData) {

    // require for window function
    window.instanceData = instanceData;

    const container = document.getElementById(instanceData.id);

    const chart = new G2.Chart({
      container: container,
      theme: instanceData.theme || 'classic'
    });

    const interaction = JSON.parse(instanceData.interaction || '[]');

    chart.options({
      type: instanceData.type || 'interval',
      width: +instanceData.width,
      height: +instanceData.height,
      autoFit: ('' + instanceData.autoFit).toLowerCase() === 'true',
      encode: { x: instanceData.xAxis, y: instanceData.yAxis, color: instanceData.color },
      data: (instanceData.series && instanceData.series[0]) || [],
    })

    if (interaction.length > 0) {
      chart.interaction(interaction);
    }
    chart.render();
  }
});