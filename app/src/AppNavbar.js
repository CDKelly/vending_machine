import React, { Component } from 'react';
import { Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink } from 'reactstrap';
import githubAvatar from './github_avatar.png';
import './AppNavbar.css';
import { Link } from 'react-router-dom';

export default class AppNavbar extends Component {
  constructor(props) {
    super(props);
    this.state = {isOpen: false};
    this.toggle = this.toggle.bind(this);
  }

  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  render() {
    return (
      <Navbar color="dark" dark expand="md">
        <NavbarBrand tag={Link} to="/vendingmachine/slots">Vending Machine</NavbarBrand>
        <NavbarToggler onClick={this.toggle}/>
        <Collapse isOpen={this.state.isOpen} navbar>
          <Nav className="ml-auto" navbar>
            <NavItem>
              <NavLink href="https://github.com/CDKelly">
                <p>CDKelly</p>
                <img src={githubAvatar} className="github-avatar" alt="GitHub Avatar" />
              </NavLink>
            </NavItem>
          </Nav>
        </Collapse>
      </Navbar>
    ); 
  }
}
