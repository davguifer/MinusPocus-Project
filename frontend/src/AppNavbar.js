import jwt_decode from "jwt-decode";
import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Collapse, Nav, NavItem, NavLink, Navbar, NavbarBrand, NavbarToggler } from 'reactstrap';
import tokenService from './services/token.service';
import useFetchData from './util/useFetchData';

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const users  = useFetchData(`/api/v1/users`, jwt);
    const puerto = window.location.port;
    

    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt])

    let adminLinks = <></>;
    let playerLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;

    const user = users.find((user) => user.username === username)
    const avatar = user ? user.avatar: 'https://www.cotopaxi.com.ec/sites/default/files/2020-08/BLANCO%20760X440PX_0.png';

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} tag={Link} to="/games">Game</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} tag={Link} to="/users">Users</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="docs" tag={Link} to="/docs">Docs</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} tag={Link} to="/achievements">Achievements</NavLink>
                    </NavItem>

                </>
            )
        }
        if (role === "PLAYER") {
            playerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="myfriend" tag={Link} to="/MyFriends" >MyFriends</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="addfriend" tag={Link} to="/SearchFriends" >SearchFriends</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="achievemnt" tag={Link} to="/MyAchievements" >MyAchievements</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="ranking" tag={Link} to="/Ranking" >Ranking</NavLink>
                    </NavItem>
                </>
            )
        }

    })

    if (!jwt) {
        publicLinks = (
            <>
                <NavItem  style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="rules" tag={Link} to="/rulesGame">Rules</NavLink>
                </NavItem>
                <NavItem  style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="register" tag={Link} to="/register">Register</NavLink>
                </NavItem>
                <NavItem  style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="login" tag={Link} to="/login">Login</NavLink>
                </NavItem>
            </>
        )
    } else {
        userLinks = (
            <>
            </>
        )
        userLogout = (
            <>
                <NavItem  style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder"}} id="rules" tag={Link} to="/rulesGame">Rules</NavLink>
                </NavItem >
                <NavItem className="d-flex"  style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="logout" tag={Link} to="/logout">Logout</NavLink>
                </NavItem>
                <NavItem style={{paddingTop:'8px'}}>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="MyProfile" tag={Link} to="/MyProfile">
                        {username}
                    </NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "black",fontFamily:"Immortal",fontWeight:"bolder" }} id="MyProfile" tag={Link} to="/MyProfile">
                        <img alt="Avatar" src={avatar} style={{ height: 40, width: 40 }} />
                    </NavLink>
                </NavItem>
            </>
        )

    }

    return (
        <div>
            {
                !window.location.href.match("field") &&
                <Navbar expand="md" light color='light'>
                    <NavbarBrand style={{ color: "black", fontFamily: "Immortal", fontWeight: "bolder" }} href="/">Home</NavbarBrand>
                    <NavbarToggler onClick={toggleNavbar} className="ms-2" />

                    <Collapse isOpen={!collapsed} navbar>
                        <Nav className="me-auto mb-2 mb-lg-0" style={{ paddingTop: '2px' }} navbar>
                            {userLinks}
                            {adminLinks}
                            {playerLinks}
                        </Nav>
                        <Nav className="mb-2 mb-lg-0" navbar>
                            {publicLinks}
                            {userLogout}
                        </Nav>
                    </Collapse>
                </Navbar>
            }
        </div>
    );
}

export default AppNavbar;
