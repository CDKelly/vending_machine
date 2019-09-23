import React, { Component } from 'react';
import { Button, Container, Form, FormGroup, Label, Input } from 'reactstrap';
// import { displayReturnChange } from './DisplayMessage';

class TransactionForm extends Component {
  emptyTransaction = {
    changeInput: '',
    selection: '',
    changeOutput: ''
  }

  constructor(props) {
    super(props);
    this.state = {
      transaction: this.emptyTransaction
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let transaction = {...this.state.transaction};
    transaction[name] = value;
    this.setState({transaction});
  }

  handleSubmit(event) {
    event.preventDefault();
    const {transaction} = this.state;

    fetch('/vendingmachine/slots', {
      method: 'PUT',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    });
  }

  handleClick(event) {
//    return displayReturnChange(this.transaction.changeInput);
  }

  render() {
    const {transaction} = this.state;

    return <Container fluid className="col-sm-6 col-md-5 col-lg-4 float-right">
      <Form className="mt-4" onSubmit={this.handleSubmit}>
      <FormGroup>
      <Label for="usersMoney">$</Label>
      <Input type="text" name="usersMoney" id="usersMoney" value={transaction.changeInput || ''}
    onChange={this.handleChange} placeholder="00.00"/>
      </FormGroup>
      <FormGroup>
      <Button color="secondary" onClick={this.handleClick} className="mt-4">Return Change</Button>
      </FormGroup>
      </Form>
    </Container>
  }
  
}

export default TransactionForm;
