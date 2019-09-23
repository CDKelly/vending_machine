import React, { Component } from 'react';
import './App.css';
import Slots from './Slots';
import AppNavbar from './AppNavbar';
import TransactionForm from './TransactionForm';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

class App extends Component {
  render() {
    return (
      <Router>
        <Switch>
          <Route path='/' exact={true} component={Slots}/>
          <Route path='/vendingmachine/slots' exact={true} component={Slots}/>
          <Route path='/vendingmachine/slots/:selectionInput/:changeInput' exact={true} component={TransactionForm}/>
        </Switch>
      </Router>
    )
  }
}

export default App;
