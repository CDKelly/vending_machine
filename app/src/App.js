import React, { Component } from 'react';
import './App.css';
import Slots from './Slots';
// import AppNavbar from './AppNavbar';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

class App extends Component {
  render() {
    return (
      <Router>
        <Switch>
          <Route path='/' exact={true} component={Slots}/>
        </Switch>
      </Router>
    )
  }
}

export default App;
