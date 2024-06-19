import {useState} from "react";
import {Link} from "react-router-dom";
import {Form, Input, Label} from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchData from "../../util/useFetchData";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserEditAdmin() {
    const emptyItem = {
        id: null,
        username: "",
        password: "",
        firstname: "",
        lastname: "",
        age: "",
        email: "",
        avatar: "",
        authority: null,
    };
    const id = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [user, setUser] = useFetchState(
        emptyItem,
        id === 'create-user' ? null : `/api/v1/users/${id}`,
        jwt,
        setMessage,
        setVisible,
        id
    );
    const auths = useFetchData(`/api/v1/users/authorities`, jwt);

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        if (name === "authority") {
            const auth = auths.find((a) => a.id === Number(value));
            setUser({...user, authority: auth});
        } else setUser({...user, [name]: value});
    }

    function handleSubmit(event) {
        event.preventDefault();

        fetch("/api/v1/users" + (user.id ? "/" + user.id : ""), {
            method: user.id ? "PUT" : "POST",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(user),
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else window.location.href = "/users";
            })
            .catch((message) => alert(message));
    }


    const modal = getErrorModal(setVisible, visible, message);
    const authOptions = auths.map((auth) => (
        <option key={auth.id} value={auth.id}>
            {auth.authority}
        </option>
    ));

    return (
        <div className={"auth-page-background"}>
            <div className="auth-page-container">
                {<h2>{user.id ? "Edit User" : "Add User"}</h2>}
                {modal}
                <div className="auth-form-container">
                    <Form onSubmit={handleSubmit}>
                        <div className="custom-form-input">
                            <Label for="username" className="custom-form-input-label">
                                Username
                            </Label>
                            <Input
                                type="text"
                                required
                                name="username"
                                id="username"
                                value={user.username || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="firstName" className="custom-form-input-label">
                                Firstname
                            </Label>
                            <Input
                                type="text"
                                required
                                name="firstName"
                                id="firstName"
                                value={user.firstName || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="lastName" className="custom-form-input-label">
                                Lastname
                            </Label>
                            <Input
                                type="text"
                                required
                                name="lastName"
                                id="lastName"
                                value={user.lastName || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="age" className="custom-form-input-label">
                                Age
                            </Label>
                            <Input
                                type="text"
                                required
                                name="age"
                                id="age"
                                value={user.age || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="email" className="custom-form-input-label">
                                Email
                            </Label>
                            <Input
                                type="text"
                                required
                                name="email"
                                id="email"
                                value={user.email || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="avatar" className="custom-form-input-label">
                                Avatar
                            </Label>
                            <Input
                                type="text"
                                required
                                name="avatar"
                                id="avatar"
                                value={user.avatar || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            <Label for="password" className="custom-form-input-label">
                                Password
                            </Label>
                            <Input
                                type="text"
                                required
                                name="password"
                                id="password"
                                value={user.password || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-form-input">
                            {user.id ? (
                                <Input
                                    type="select"
                                    disabled
                                    name="authority"
                                    id="authority"
                                    value={user.authority?.id || ""}
                                    onChange={handleChange}
                                    className="custom-input"
                                >
                                    <option value="">None</option>
                                    {authOptions}
                                </Input>
                            ) : (
                                <Input
                                    type="select"
                                    required
                                    name="authority"
                                    id="authority"
                                    value={user.authority?.id || ""}
                                    onChange={handleChange}
                                    className="custom-input"
                                >
                                    <option value="">None</option>
                                    {authOptions}
                                </Input>
                            )}
                        </div>
                        <div className="custom-button-row">
                            <Link
                                to={`/MyProfile/` + user.id}
                                className="auth-button"
                                style={{textDecoration: "none"}}
                                onClick={handleSubmit}
                            >
                                Save
                            </Link>
                            <Link
                                to={`/MyProfile/` + user.id}
                                className="auth-button"
                                style={{textDecoration: "none"}}
                            >
                                Cancel
                            </Link>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    );
}
