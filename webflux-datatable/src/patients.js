import React from "react"
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
} from 'recharts';

class PatientList extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            data: [],
            statistic: {
                contaminated: 0,
                recovered: 0,
                timestamp: 0,
                total: 0
            },
            statistics: [{
                contaminated: 0,
                recovered: 0,
                timestamp: Date.now(),
                total: 0
            }]
        };
    }

    updateStatistic() {
        const { data, statistics } = this.state;
        let contaminated = 0;
        let recovered = 0;
        data.forEach((p) => {
            if (p.contaminated) {
                contaminated++;
            }
            if (p.recovered) {
                recovered++;
            }
        });
        const stat = {
            contaminated: contaminated,
            recovered: recovered,
            timestamp: Date.now(),
            total: data.length
        };
        const stats = [...statistics];
        stats.push(stat);
        this.setState({ statistic: stat });
        this.setState({ statistics: stats });
    }

    render() {
        const { error, isLoaded, data, statistic, statistics } = this.state;
        if (error != null) {
            return <div>Erreur : {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Chargement…</div>;
        } else {
            return (<div style={{ display: 'flex' }}>
                <div style={{ flex: '50 %', padding: '10px' }}>
                    <table className="table table-hover">
                        <thead className="thead-dark">
                            <tr>
                                <th>Id</th>
                                <th>Firstname</th>
                                <th>Lastname</th>
                                <th>Contaminated</th>
                                <th>Recovered</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((p) => (
                                <tr key={p.id} style={{ backgroundColor: p.contaminated ? 'red' : p.recovered ? 'green' : 'white' }}>
                                    <td>{p.id}</td>
                                    <td>{p.firstName}</td>
                                    <td>{p.lastName}</td>
                                    <td>{"" + p.contaminated}</td>
                                    <td>{"" + p.recovered}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div style={{ flex: '25 %', padding: '10px 10px 10px 50px' }}>
                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th>Contaminated</th>
                                <th>% contaminated</th>
                                <th>Recovered</th>
                                <th>% recovered</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>{statistic.contaminated}</td>
                                <td>{statistic.contaminated / statistic.total * 100}</td>
                                <td>{statistic.recovered}</td>
                                <td>{statistic.recovered / statistic.total * 100}</td>
                                <td>{statistic.total}</td>
                            </tr>
                        </tbody>
                    </table>
                    <LineChart width={500} height={300} data={statistics} style={{paddingTop: '50px' }}>
                        <XAxis dataKey="timestamp" tickFormatter={this.formatXAxis} />
                        <YAxis />
                        <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
                        <Line type="monotone" dataKey="contaminated" stroke="#ca8282" />
                        <Line type="monotone" dataKey="recovered" stroke="#82ca9d" />
                    </LineChart>
                </div>
            </div>
            );
        }
    }

    formatXAxis(item) {
        return new Date(item).toLocaleTimeString();
    }

    componentDidMount() {
        fetch("http://localhost:8080/patients")
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        data: result
                    });
                },
                (error) => {
                    this.setState({
                        isLoaded: true,
                        error
                    });
                }
            );

        let eventSource = new EventSource("http://localhost:8080/heal/stream")
         eventSource.onmessage = e => updatePatientList(JSON.parse(e.data));

        const updatePatientList = (patient) => {
            const { data } = this.state;

            let changed = false;
            data.forEach(p => {
                if (p.id === patient.id) {
                    changed |= p.recovered != patient.recovered || p.contaminated != patient.contaminated;
                    p.recovered = patient.recovered;
                    p.contaminated = patient.contaminated;
                }
            });
            if (changed) {
                this.setState(data);
                this.updateStatistic();
            }
        }
    }
}

export default PatientList;
