import jwt_decode from "jwt-decode";
import React from "react";
import { ErrorBoundary } from "react-error-boundary";
import { Route, Routes } from "react-router-dom";
import AppNavbar from "./AppNavbar";
import AchievementEdit from "./achievement/achievementEdit";
import AchievementList from "./achievement/achievementList";
import Achievement from "./achievement/myAchievements";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import UserListAdmin from "./admin/users/UserListAdmin";
import Login from "./auth/login";
import Logout from "./auth/logout";
import Register from "./auth/register";
import ExpecteView from "./field/expecteView";
import Field from "./field/index";
import Friend from "./friends/index";
import SearchFriends from "./friends/searchFriend";
import GameList from "./game";
import CreateGame from "./game/createGame";
import EditGame from "./game/editGame";
import Home from "./home";
import Lobby from "./lobby/index";
import PrivateRoute from "./privateRoute";
import Profile from "./profile";
import MyProfileEdit from "./profile/myProfile/MyProfileEdit";
import MyProfileList from "./profile/myProfile/myProfileList";
import SwaggerDocs from "./public/swagger";
import RulesGame from "./rulesGame/Index";
import tokenService from "./services/token.service";
import Ranking from "./ranking/index";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let playerRoutes = <></>;
  let userRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/achievements/" exact={true} element={<PrivateRoute><AchievementList /></PrivateRoute>} />
          <Route path="/achievements/:achievementId" exact={true} element={<PrivateRoute><AchievementEdit/></PrivateRoute>} />
          <Route path="/lobby/:id" element={<Lobby />} />
          <Route path="/UserListAdmin/:id" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/MyProfileEdit/:id" exact={true} element={<PrivateRoute><MyProfileEdit /></PrivateRoute>} />
          <Route path="/CreateGames" element={<CreateGame />} />
          <Route path="/EditGames/:id" element={<EditGame />} />
          <Route path="/register" element={<Register />} />

          </>)
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/MyFriends" element={<Friend />} />
          <Route path="/SearchFriends" element={<SearchFriends />} />
          <Route path="/CreateGames" element={<CreateGame />} />
          <Route path="/lobby/:id" element={<Lobby />} />
          <Route path="/MyProfileEdit/:id" exact={true} element={<PrivateRoute><MyProfileEdit /></PrivateRoute>} />
          <Route path="/field/:id" element={<Field />} />
          <Route path="/achievements/" exact={true} element={<PrivateRoute><AchievementList /></PrivateRoute>} />
          <Route path="/MyAchievements" element={<Achievement />} />
          <Route path="/Ranking" element={<Ranking />} />
        </>)
    }
  })
  if (!jwt) {
    publicRoutes = (
      <>        
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>
        {/* <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} /> */}  
        <Route path="/MyProfile" exact={true} element={<MyProfileList />} />
        <Route path="/games" element={<GameList />} />      
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="/field/:id/expecte" element={<ExpecteView />}/>
      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          <Route path="/profile" exact={true} element={<Profile />} />
          <Route path="/docs" element={<SwaggerDocs />} />
          <Route path="/rulesGame" element={<RulesGame />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
