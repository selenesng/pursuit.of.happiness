import React, { Component } from 'react'
import {Redirect} from "react-router-dom";

class NewUserForm extends Component {

    state = {
        user: {},
        redirectToUsersPage: false
    }

    handleSubmit = (event) => {
        event.preventDefault();

        this.props.createUser(this.state.user)

        this.setState({redirectToUsersPage: true})
    }

    handleChange = (event) => {
        const attributeToChange = event.target.name
        const newValue = event.target.value

        const updatedUser = { ...this.state.user }
        updatedUser[attributeToChange] = newValue
        this.setState({ user: updatedUser })
    }

    render() {

        if(this.state.redirectToUsersPage) {
            return <Redirect to="/" />
        }

        return (
            <div>
                <h2>Create New User</h2>
                <form onSubmit={this.handleSubmit} id="new-user-form">
                    <div>
                        <label htmlFor="userName">Username </label>
                        <input
                            id="new-user-user-name"
                            type="text"
                            name="userName"
                            onChange={this.handleChange} />
                    </div>

                    <div>
                        <label htmlFor="firstName">First Name </label>
                        <input
                            id="new-user-first-name"
                            type="text"
                            name="firstName"
                            onChange={this.handleChange} />
                    </div>

                    <div>
                        <label htmlFor="lastName">Last Name </label>
                        <input
                            id="new-user-last-name"
                            type="text"
                            name="lastName"
                            onChange={this.handleChange} />
                    </div>

                    <div>
                        <input
                            id="new-user-submit"
                            type="submit"
                            value="Create" />
                    </div>
                </form>
            </div>
        )
    }

}

export default NewUserForm