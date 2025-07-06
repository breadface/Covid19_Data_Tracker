import React, { useEffect, useRef } from 'react';
import * as d3 from 'd3';

interface Covid19DataPoint {
  date: Date;
  cases: number;
  deaths: number;
  country: string;
}

interface CustomSVGChartProps {
  data: Covid19DataPoint[];
  width?: number;
  height?: number;
  title: string;
}

const CustomSVGChart: React.FC<CustomSVGChartProps> = ({ 
  data, 
  width = 600, 
  height = 400, 
  title 
}: CustomSVGChartProps) => {
  const svgRef = useRef<SVGSVGElement>(null);

  useEffect(() => {
    if (!data || data.length === 0 || !svgRef.current) return;

    // Clear previous content
    d3.select(svgRef.current).selectAll("*").remove();

    const svg = d3.select(svgRef.current);
    const margin = { top: 40, right: 40, bottom: 60, left: 60 };
    const chartWidth = width - margin.left - margin.right;
    const chartHeight = height - margin.top - margin.bottom;

    // Create chart group
    const g = svg.append("g")
      .attr("transform", `translate(${margin.left},${margin.top})`);

    // Parse dates
    const processedData = data.map((d: Covid19DataPoint) => ({
      ...d,
      date: d.date instanceof Date ? d.date : new Date(d.date)
    }));

    // Scales
    const xScale = d3.scaleTime()
      .domain(d3.extent(processedData, (d: Covid19DataPoint) => d.date) as [Date, Date])
      .range([0, chartWidth]);

    const yScale = d3.scaleLinear()
      .domain([0, d3.max(processedData, (d: Covid19DataPoint) => Math.max(d.cases, d.deaths)) || 0])
      .range([chartHeight, 0]);

    // Color scale (used for legend)
    const colorScale = d3.scaleOrdinal<string>()
      .domain(['cases', 'deaths'])
      .range(['#8884d8', '#ff7300']);

    // Line generators
    const lineGenerator = d3.line<Covid19DataPoint>()
      .x((d: Covid19DataPoint) => xScale(d.date))
      .y((d: Covid19DataPoint) => yScale(d.cases))
      .curve(d3.curveMonotoneX);

    const deathLineGenerator = d3.line<Covid19DataPoint>()
      .x((d: Covid19DataPoint) => xScale(d.date))
      .y((d: Covid19DataPoint) => yScale(d.deaths))
      .curve(d3.curveMonotoneX);

    // Add gradient definitions
    const defs = svg.append("defs");
    
    // Gradient for cases line
    const casesGradient = defs.append("linearGradient")
      .attr("id", "casesGradient")
      .attr("gradientUnits", "userSpaceOnUse")
      .attr("x1", 0)
      .attr("y1", yScale(d3.max(processedData, (d: Covid19DataPoint) => d.cases) ?? 0))
      .attr("x2", 0)
      .attr("y2", yScale(0));

    casesGradient.append("stop")
      .attr("offset", "0%")
      .attr("stop-color", "#8884d8")
      .attr("stop-opacity", 0.8);

    casesGradient.append("stop")
      .attr("offset", "100%")
      .attr("stop-color", "#8884d8")
      .attr("stop-opacity", 0.1);

    // Gradient for deaths line
    const deathsGradient = defs.append("linearGradient")
      .attr("id", "deathsGradient")
      .attr("gradientUnits", "userSpaceOnUse")
      .attr("x1", 0)
      .attr("y1", yScale(d3.max(processedData, (d: Covid19DataPoint) => d.deaths) ?? 0))
      .attr("x2", 0)
      .attr("y2", yScale(0));

    deathsGradient.append("stop")
      .attr("offset", "0%")
      .attr("stop-color", "#ff7300")
      .attr("stop-opacity", 0.8);

    deathsGradient.append("stop")
      .attr("offset", "100%")
      .attr("stop-color", "#ff7300")
      .attr("stop-opacity", 0.1);

    // Add area paths
    const areaGenerator = d3.area<Covid19DataPoint>()
      .x((d: Covid19DataPoint) => xScale(d.date))
      .y0(chartHeight)
      .y1((d: Covid19DataPoint) => yScale(d.cases))
      .curve(d3.curveMonotoneX);

    const deathAreaGenerator = d3.area<Covid19DataPoint>()
      .x((d: Covid19DataPoint) => xScale(d.date))
      .y0(chartHeight)
      .y1((d: Covid19DataPoint) => yScale(d.deaths))
      .curve(d3.curveMonotoneX);

    // Add areas
    g.append("path")
      .datum(processedData)
      .attr("fill", "url(#casesGradient)")
      .attr("d", areaGenerator);

    g.append("path")
      .datum(processedData)
      .attr("fill", "url(#deathsGradient)")
      .attr("d", deathAreaGenerator);

    // Add lines
    g.append("path")
      .datum(processedData)
      .attr("fill", "none")
      .attr("stroke", "#8884d8")
      .attr("stroke-width", 3)
      .attr("d", lineGenerator);

    g.append("path")
      .datum(processedData)
      .attr("fill", "none")
      .attr("stroke", "#ff7300")
      .attr("stroke-width", 3)
      .attr("d", deathLineGenerator);

    // Add dots for interaction
    const dots = g.selectAll<SVGCircleElement, Covid19DataPoint>(".dot")
      .data(processedData)
      .enter()
      .append("circle")
      .attr("class", "dot")
      .attr("cx", (d: Covid19DataPoint) => xScale(d.date))
      .attr("cy", (d: Covid19DataPoint) => yScale(d.cases))
      .attr("r", 4)
      .attr("fill", "#8884d8")
      .attr("opacity", 0)
      .on("mouseover", function(this: SVGCircleElement, event: MouseEvent, d: Covid19DataPoint) {
        d3.select(this as SVGCircleElement)
          .attr("opacity", 1)
          .attr("r", 6);

        // Show tooltip
        const tooltip = d3.select("body").append("div")
          .attr("class", "tooltip")
          .style("position", "absolute")
          .style("background", "rgba(0, 0, 0, 0.8)")
          .style("color", "white")
          .style("padding", "8px")
          .style("border-radius", "4px")
          .style("font-size", "12px")
          .style("pointer-events", "none");

        tooltip.html(`
          <strong>${d3.timeFormat("%Y-%m-%d")(d.date)}</strong><br/>
          Cases: ${d.cases.toLocaleString()}<br/>
          Deaths: ${d.deaths.toLocaleString()}<br/>
          Country: ${d.country}
        `)
          .style("left", (event.pageX + 10) + "px")
          .style("top", (event.pageY - 10) + "px");
      })
      .on("mouseout", function(this: SVGCircleElement) {
        d3.select(this as SVGCircleElement)
          .attr("opacity", 0)
          .attr("r", 4);
        d3.selectAll(".tooltip").remove();
      });

    // Add death dots
    const deathDots = g.selectAll<SVGCircleElement, Covid19DataPoint>(".death-dot")
      .data(processedData)
      .enter()
      .append("circle")
      .attr("class", "death-dot")
      .attr("cx", (d: Covid19DataPoint) => xScale(d.date))
      .attr("cy", (d: Covid19DataPoint) => yScale(d.deaths))
      .attr("r", 4)
      .attr("fill", "#ff7300")
      .attr("opacity", 0)
      .on("mouseover", function(this: SVGCircleElement, event: MouseEvent, d: Covid19DataPoint) {
        d3.select(this as SVGCircleElement)
          .attr("opacity", 1)
          .attr("r", 6);
      })
      .on("mouseout", function(this: SVGCircleElement) {
        d3.select(this as SVGCircleElement)
          .attr("opacity", 0)
          .attr("r", 4);
      });

    // Add axes
    const xAxis = d3.axisBottom(xScale)
      .ticks(8);

    const yAxis = d3.axisLeft(yScale)
      .ticks(6);

    g.append("g")
      .attr("transform", `translate(0,${chartHeight})`)
      .call(xAxis as any)
      .selectAll("text")
      .style("text-anchor", "end")
      .attr("dx", "-.8em")
      .attr("dy", ".15em")
      .attr("transform", "rotate(-45)");

    g.append("g")
      .call(yAxis as any);

    // Add grid lines
    g.append("g")
      .attr("class", "grid")
      .attr("transform", `translate(0,${chartHeight})`)
      .call(d3.axisBottom(xScale)
        .tickSize(-chartHeight)
        .tickFormat(() => "")
        .ticks(8))
      .style("stroke-dasharray", "3,3")
      .style("opacity", 0.3);

    g.append("g")
      .attr("class", "grid")
      .call(d3.axisLeft(yScale)
        .tickSize(-chartWidth)
        .tickFormat(() => "")
        .ticks(6))
      .style("stroke-dasharray", "3,3")
      .style("opacity", 0.3);

    // Add legend
    const legend = svg.append("g")
      .attr("transform", `translate(${width - 150}, 20)`);

    legend.append("rect")
      .attr("x", 0)
      .attr("y", 0)
      .attr("width", 15)
      .attr("height", 15)
      .attr("fill", "#8884d8");

    legend.append("text")
      .attr("x", 20)
      .attr("y", 12)
      .text("Cases")
      .style("font-size", "12px")
      .style("fill", "#333");

    legend.append("rect")
      .attr("x", 0)
      .attr("y", 25)
      .attr("width", 15)
      .attr("height", 15)
      .attr("fill", "#ff7300");

    legend.append("text")
      .attr("x", 20)
      .attr("y", 37)
      .text("Deaths")
      .style("font-size", "12px")
      .style("fill", "#333");

    // Add title
    svg.append("text")
      .attr("x", width / 2)
      .attr("y", 20)
      .attr("text-anchor", "middle")
      .style("font-size", "16px")
      .style("font-weight", "bold")
      .style("fill", "#333")
      .text(title);

  }, [data, width, height, title]);

  return (
    <div className="custom-svg-chart">
      <svg
        ref={svgRef}
        width={width}
        height={height}
      />
    </div>
  );
};

export default CustomSVGChart; 