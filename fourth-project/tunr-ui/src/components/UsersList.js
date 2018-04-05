import React, { Component } from 'react'
import User from './User'
import {Link} from "react-router-dom";

const UsersList = (props) => {
    return (
        <div id="users-wrapper">
            <h1>Users</h1>

            <Link to="/new" id="new-user-link">Create New User</Link>
            {
                props.users.map((user, index) => {
                    return (
                        <User
                            deleteUser={props.deleteUser}
                            user={user}
                            key={index}
                            index={index} />
                    )
                })
            }
        </div>
    )
}

export default UsersList