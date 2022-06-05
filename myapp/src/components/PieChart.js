import React from 'react'
import axios from 'axios';
import CanvasJSReact from './canvasjs.react'


var CanvasJs = CanvasJSReact.CanvasJs
var CanvasJSChart = CanvasJSReact.CanvasJSChart

const url = 'http://localhost:8080/v2/stream/scores';
class PieChart extends React.Component {
    state = {
        scores: []
    }
    componentDidMount() {
        axios.get(url)
            .then(
                res => {
                    const scores = res.data;
                    this.setState({scores});
                    console.log(res.data)
                }
            ).catch(err => {
                console.error(err)
            })
    }
    render() {
        const data = this.state.scores
        console.log(data)
        const options = {
            theme: "dark2",
			exportFileName: "Doughnut Chart",
			exportEnabled: true,
			animationEnabled: true,
            title: {
              text: "Sentiments"
            },
            // legend: {
            //     cursor: "pointer",
            //     itemclick: explodePie
            // },
            data: [{				
                type: "doughnut",
				innerRadius: 90,
				showInLegend: true,
				toolTipContent: "<b>{name}</b>: {y} (#percent%)",
				indexLabel: "{name} - #percent%",
                dataPoints: this.state.scores
            }]
        }
        return (
            <div>
              <CanvasJSChart options = {options}
                  /* onRef = {ref => this.chart = ref} */
              />
            </div>
        );
    }
}

export default PieChart;