import React, { Component } from 'react';
import { Container, Alert } from 'reactstrap';

class DisplayMessage extends Component {
  constructor(props) {
    super(props);
    this.state = {message: '', display: null, alertColor: 'primary'};

//    this.displayReturnChange = this.displayReturnChange.bind(this);
  }

  // function displayReturnChange(changeInput) {
  //   return this.setState = (message: "Please take any change (${changeInput}) in the change chute", display: true, alertColor: 'primary');
  // };

  render() {
    const {message, display, alertColor} = this.state;

    return (
      <Container fluid className="col-sm-6 col-md-6 col-lg-6 mt-4">
        <Alert isOpen={display} color={alertColor}>{message}</Alert>
      </Container>
    );
  }
}


//export displayReturnChange;
export default DisplayMessage;
