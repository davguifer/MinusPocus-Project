import {Link} from "react-router-dom";
import {Button, ButtonGroup, Table} from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import useFetchData from "../../util/useFetchData";

const jwt = tokenService.getLocalAccessToken();

export default function MyProfileList() {
    const id = tokenService.getUser().id;
    const user = useFetchData(`/api/v1/users/${id}`, jwt);
    const stats = useFetchData(`/api/v1/stats/${id}`, jwt);
    const avatar = user ? user.avatar : 'https://www.cotopaxi.com.ec/sites/default/files/2020-08/BLANCO%20760X440PX_0.png';
    const userList =
        <tbody>
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

        {(user.authority ? user.authority.authority === "PLAYER" : false) && <tr>
            <th scope="row">Games played</th>
            <td>{stats.gamesPlayed}</td>
        </tr>}
        {(user.authority ? user.authority.authority === "PLAYER" : false) && <tr>
            <th scope="row">Victories</th>
            <td>{stats.victories}</td>
        </tr>}
        {(user.authority ? user.authority.authority === "PLAYER" : false) && <tr>
            <th scope="row">Time played</th>
            <td>{(stats.timePlayed / 3600).toFixed(2) + " hours"}</td>
        </tr>}

        <tr>
            <th scope="row" style={{paddingTop: 130}}>Avatar</th>
            <td>
                <img alt="" src={avatar} style={{height: 300, width: 300, display: 'block', margin: 'auto'}}/>
            </td>
        </tr>
        <td colSpan="2" style={{textAlign: 'center', backgroundColor: 'transparent'}}>
            <ButtonGroup style={{backgroundColor: 'transparent'}}>
                <Button
                    size="sm"
                    color="primary"
                    aria-label={"edit-" + user.id}
                    tag={Link}
                    onClick={() => window.location.href = "/MyProfileEdit/" + user.id}
                    style={{padding: 15, width: 450, borderRadius: 10, margin: 2}}
                >
                    Edit
                </Button>
            </ButtonGroup>
        </td>
        </tbody>

    return (
        <div className={"auth-page-background"}>
            <div className="admin-page-container">
                <div>
                    <Table aria-label="users" className="mt-4">
                        {userList}
                    </Table>
                </div>
            </div>
        </div>
    );
}
