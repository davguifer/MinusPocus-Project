import React, {useState} from 'react';
import {Link} from "react-router-dom";
import {Button, ButtonGroup, Table} from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import useFetchState from "../../util/useFetchState";
import {useEffect} from 'react';

const jwt = tokenService.getLocalAccessToken();


export default function UserListAdmin() {

    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);


    const [users, setUsers] = useFetchState(
        [],
        `/api/v1/users`,
        jwt,
        setMessage,
        setVisible
    );


    const userList = users.map((user) => {
        const avatar = user ? user.avatar : 'https://www.cotopaxi.com.ec/sites/default/files/2020-08/BLANCO%20760X440PX_0.png';

        return (
            <tbody key={user.id}>
            <tr>
                <th scope="row">Username</th>
                <td>{user.username}</td>
            </tr>
            <tr>
                <th scope="row">Firstname</th>
                <td>{user.firstName}</td>
            </tr>
            <tr>
                <th scope="row">Lastname</th>
                <td>{user.lastName}</td>
            </tr>
            <tr>
                <th scope="row">Age</th>
                <td>{user.age}</td>
            </tr>
            <tr>
                <th scope="row">Email</th>
                <td>{user.email}</td>
            </tr>
            <tr>
                <th scope="row" style={{paddingTop: 130}}>Avatar</th>
                <td>
                    <img alt="" src={avatar} style={{height: 300, width: 300, display: 'block', margin: 'auto'}}/>
                </td>
            </tr>
            <tr>
                <td colSpan="2" style={{textAlign: 'center', backgroundColor: 'transparent'}}>
                    <ButtonGroup>
                        <Button
                            size="sm"
                            color="primary"
                            aria-label={"edit-" + user.id}
                            tag={Link}
                            to={"/UserListAdmin/" + user.id}
                            style={{padding: 15, width: 450, borderRadius: 10, margin: 2}}
                        >
                            Edit
                        </Button>
                        <Button
                            aria-label={"delete-" + user.id}
                            size="sm"
                            color="danger"
                            style={{padding: 15, width: 450, borderRadius: 10, margin: 2}}
                            onClick={() => {
                                deleteFromList(
                                    `/api/v1/users/${user.id}`,
                                    user.id,
                                    [users, setUsers],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible
                                )
                            }
                            }
                        >
                            Delete
                        </Button>
                    </ButtonGroup>
                </td>
            </tr>
            </tbody>
        );
    });


    return (
        <div className={"auth-page-background"}>
            <div className="admin-page-container">
                <ButtonGroup style={{marginTop: 20}}>
                    <Button size="sm"
                            color='success'
                            aria-label={"create-new-user"}
                            tag={Link}
                            to={`/register`}
                            style={{padding: 15, marginTop: 20, width: 600, borderRadius: 10, margin: 2}}>
                        Create User
                    </Button>
                </ButtonGroup>
                <div className='user-list-tables'>
                    <Table>
                        {userList}
                    </Table>
                </div>
            </div>
        </div>
    );
}
