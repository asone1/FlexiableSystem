import { hot } from 'react-hot-loader';
import React, { Component } from 'react';
import axios from 'axios';

type appState = {
  id: String,
  name: String
}
class App extends Component<{}, appState> {

  constructor(props) {
    super(props);

  }
  componentWillMount() {
    var result;
    let url = 'http://localhost:8081/api/class/order';
    const myClass = axios.get(url).then(
      res => {
        result = res.data;
        console.log(result);
        console.log(result.classId); console.log(result.className);
        this.changeName(result.className);
      });
  }
  changeName = (name) => { this.setState({ name: name }); console.log(name) };

  render() {

    if (this.state) return <div>{this.state.name}</div>;
    else return <div> 'wait time' </div>
  }
}


export default hot(module)(App);