import React, { Component } from 'react';
import { Button, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class Slots extends Component {

  constructor(props) {
    super(props);
    this.state = {slots: [], isLoading: true};
  }

  componentDidMount() {
    this.setState({isLoading: true});

    fetch('/vendingmachine/slots')
      .then(response => response.json())
      .then(data => this.setState({slots: data, isLoading: false}));
  }

  // async remove(id) {
  //   await fetch(`/api/slot/${id}`, {
  //     method: 'DELETE',
  //     headers: {
  //       'Accept': 'application/json',
  //       'Content-Type': 'application/json'
  //     }
  //   }).then(() => {
  //     let updatedSlots = [...this.state.slots].filter(i => i.id !== id);
  //     this.setState({slots: updatedSlots});
  //   });
	// }



  render() {
    const {slots, isLoading} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    const slotCells = slots.map(slot => {
      const formatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2
      });
      
      return (
          <td key={slot.id} className="align-middle">
          <p>{slot.productName}</p>
          <p>{formatter.format(slot.price)}</p>
          <p>Remaining: {slot.currentQuantity}</p>
          </td>        
      );
    });

    function sliceRows(array, size) {
      let sliced_arr = [];
      let idx = 0;
      while (idx < array.length) {
        sliced_arr.push(array.slice(idx, idx + size));
        idx += size;
      }

      let rows = sliced_arr.map(row => {
        return <tr>{row}</tr>;
      });

      return rows;
    }

    return (
        <div>
          <Container fluid>
            <Table hover className="mt-4">
              <thead>
                <tr>
                  <th></th>
                  <th></th>
                  <th></th>
                  <th rowSpan={slotCells.length/3}>This is a test</th>
                </tr>
              </thead>
              <tbody>
                {sliceRows(slotCells, 3)}
              </tbody>
            </Table>
          </Container>
        </div>
    );
  }
}

export default Slots;
