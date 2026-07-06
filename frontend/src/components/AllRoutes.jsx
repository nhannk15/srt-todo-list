import React from 'react'
import { Route, Routes } from 'react-router'
import LoginPage from "../pages/LoginPage";
import HomePage from "../pages/HomePage";
import UndoneTasks from "../components/UndoneTasks";
import DoneTasks from "../components/DoneTasks";
import AllTasks from "../components/AllTasks";


export default function AllRoutes() {
    return (
        <>
            <Routes>
                <Route path='/login' element={<LoginPage />}/>
                <Route path='/' element={<HomePage />}>
                    <Route path='undone-tasks' element={<UndoneTasks />}/>
                    <Route path='done-tasks' element={<DoneTasks />}/>
                    <Route path='all-tasks' element={<AllTasks />}/>
                </Route>
            </Routes>
        </>
    )
}
