import React, { Component } from 'react';
import { Button, Container, Form, FormGroup, Label, Input } from 'reactstrap';
// import { displayReturnChange } from './DisplayMessage';

class TransactionForm extends Component {
  emptyTransaction = {
    changeInput: 0,
    selectionInput: '',
    changeOutput: 0
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

    fetch("/vendingmachine/slots/${transaction.selectionInput}/${transaction.changeInput}", {
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
          <Label for="changeInput">$</Label>
          <Input type="number" name="changeInput" id="changeInput" value={transaction.changeInput || ''} onChange={this.handleChange} placeholder="00.00"/>
        </FormGroup>
        <FormGroup>
          <Button color="primary" name="changeInput" id="changeInputQuarter" value={transaction.changeInput + 0.25} as="input" type="button" onChange={this.handleChange} className="mt-4 mr-2 mb-4">Add Quarter</Button>
          <Button color="primary" name="changeInput" id="changeInputDime" value={transaction.changeInput + 0.10} as="input" type="button" onChange={this.handleChange} className="mt-4 mr-2 mb-4">Add Dime</Button>
          <Button color="primary" name="changeInput" id="changeInputNickel" value={transaction.changeInput + 0.05} as="input" type="button" onChange={this.handleChange} className="mt-4 mr-2 mb-4">Add Nickel</Button>
          <Button color="primary" name="changeInput" id="changeInputPenny" value={transaction.changeInput + 0.01} as="input" type="button" onChange={this.handleChange} className="mt-4 mr-2 mb-4">Add Penny</Button>
        </FormGroup>
        <FormGroup>
          <Button color="secondary" onClick={this.handleClick} className="mt-4 mb-4">Return Change</Button>
        </FormGroup>
        <hr style={{ color: "grey", backgroundColor: "grey", height: 1 }} />
        <FormGroup>
          <Label for="selectInput" className="mt-4">Slot Id:</Label>
          <Input type="number" name="selectionInput" id="selectionInput" value={transaction.selectionInput || ''}
        onChange={this.handleChange} placeholder="Enter Slot Id"/>
        </FormGroup>
        <FormGroup>
          <Button color="success" onClick={this.handleSubmit} className="mt-4 mb-4">Make Selection</Button>
        </FormGroup>
      </Form>
    </Container>
  }
  
}

export default TransactionForm;
