import {Table, Button } from "reactstrap";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useEffect, useState } from "react";

const imgnotfound = "https://cdn-icons-png.flaticon.com/512/5778/5778223.png";
const jwt = tokenService.getLocalAccessToken();

export default function AchievementList() {
    const currentUserId = tokenService.getUser().id;
    const [achievements, setAchievements] = useFetchState([], `/api/v1/achievements`,jwt);
    const [stats, setStats] = useFetchState([], `/api/v1/stats/${currentUserId}`,jwt)
    const [claimed, setClaimed] = useState({"claimed": [], "all": []})

    useEffect(() => {
        if(stats.user){
            setClaimed({"claimed": stats.user.achievement.map((x) => x.id), "all": achievements})
        }
    }, [stats, achievements])
    
    const achievementList = (claimed.all.length > 0) ? claimed.all.map((a) => {
        return (
            <tr key={a.id}>
                <td className="text-center">{a.name}</td>
                <td className="text-center"> {a.description} </td>
                <td className="text-center">
                    <img src={a.badgeImage? a.badgeImage : imgnotfound } alt={a.name} width="50px"/>
                </td>
                <td className="text-center">
                    {(!wasClaim(a.id) && !claimable(a)) && "Not completed"}
                    {(!wasClaim(a.id) && claimable(a)) && <Button className={"claim-button"} onClick={() => {
                        claimAchievement(a.id)
                    }}>Claim</Button>}
                    {wasClaim(a.id) && "Completed"}
                </td>
            </tr>
        );
    }): null;

    function wasClaim(id){
        return claimed.claimed.includes(id);
    }

    function claimAchievement(id){
        fetch("/api/v1/achievements/" + id + '/claim', {
            method: "PUT", headers: {
                Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
            }, body: currentUserId
        }).then((response) => {
            let c = claimed.claimed;
            c.push(id);
            setClaimed({"claimed": c, "all": claimed.all});
        })
    }

    function claimable(achievement){
        let claimable = true;
        switch(achievement.metric){
            case "GAMES_PLAYED":
                claimable = claimable && (stats.gamesPlayed >= achievement.threshold);
            break;
            case "VICTORIES":
                claimable = claimable && (stats.victories >= achievement.threshold);
            break;
            case "LOSES":
                claimable = claimable && ((stats.gamesPlayed - stats.victories) >= achievement.threshold);
            break;
            case "TOTAL_PLAY_TIME":
                claimable = claimable && (stats.timePlayed >= achievement.threshold);
            break;
            default:
            break;
        }
        return claimable;
    }

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Achievements</h1>
                <div className="table-responsive">
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Name</th>
                                <th className="text-center">Description</th>
                                <th className="text-center">Image</th>
                                <th className="text-center">State</th>
                            </tr>
                        </thead>
                        <tbody>{achievementList}</tbody>
                    </Table>
                </div>
            </div>
        </div>
    );
}