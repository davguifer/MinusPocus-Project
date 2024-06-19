import {Table, Button} from "reactstrap";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import {Link} from "react-router-dom";
import deleteFromList from "./../util/deleteFromList";
import {useState} from "react";
import getErrorModal from "./../util/getErrorModal";
import "./../static/css/auth/authPage.css"


const imgnotfound = "https://cdn-icons-png.flaticon.com/512/5778/5778223.png";
const jwt = tokenService.getLocalAccessToken();

export default function AchievementList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);

    const [achievements, setAchievements] = useFetchState(
        [],
        `/api/v1/achievements`,
        jwt
    );
    const achievementList =
        achievements.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.name}</td>
                    <td className="text-center"> {a.description} </td>
                    <td className="text-center">
                        <img src={a.badgeImage ? a.badgeImage : imgnotfound} alt={a.name} width="50px"/>
                    </td>
                    <td className="text-center"> {a.threshold} </td>
                    <td className="text-center"> {a.metric} </td>
                    <td>
                        <td className="text-center">
                            <Button outline color="warning" style={{width: 120}}>
                                <Link
                                    to={`/achievements/` + a.id} className="btn sm"
                                    style={{textDecoration: "none"}}>Edit</Link>
                            </Button>
                        </td>
                        <td className="text-center">
                            <Button outline color="danger" style={{width: 125, height: 50}}
                                    onClick={() =>
                                        deleteFromList(
                                            `/api/v1/achievements/${a.id}`,
                                            a.id,
                                            [achievements, setAchievements],
                                            [alerts, setAlerts],
                                            setMessage,
                                            setVisible
                                        )}>
                                Delete
                            </Button>
                        </td>
                    </td>
                </tr>
            );
        });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className={"auth-page-background"}>
            <div className="admin-page-container">
                <h1 className="text-center">Achievements</h1>
                <div className="table-responsive">
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                        <tr>
                            <th className="text-center">Name</th>
                            <th className="text-center">Description</th>
                            <th className="text-center">Image</th>
                            <th className="text-center">Threshold</th>
                            <th className="text-center">Metric</th>
                            <th className="text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody>{achievementList}</tbody>
                    </Table>
                    <Button outline color="success">
                        <Link
                            to={`/achievements/new`} className="btn sm"
                            style={{textDecoration: "none"}}>Create achievement</Link>
                    </Button>
                </div>
            </div>
        </div>
    );
}
