import React from 'react'
import ReactDOM from 'react-dom/client'
import {
    Navigate, RouterProvider, createBrowserRouter,
} from "react-router-dom"
import MainLayout from '../layout/MainLayout'
import { worker } from "../mocks/browser"
import store from './store'
import { Provider } from 'react-redux'
import ErrorPage from '../layout/ErrorPage'
import Chat from '../components/Chat'
import { ChannelType } from '../models/channel'
import FriendList from '../components/FriendList'
import GroupList from '../components/GroupList'
import { Notifications } from '@mantine/notifications'
import { MantineProvider } from '@mantine/core'

const router = createBrowserRouter([
    {
        path: "/",
        element: <Navigate to="/chat" replace />,
        errorElement: <ErrorPage />,
    },
    {
        path: "/chat",
        element: <MainLayout />,
        errorElement: <ErrorPage />,
        children: [
            {
                path: "friends",
                element: <FriendList />,
            },
            {
                path: "groups",
                element: <GroupList />,
            },
            {
                path: "channels/u/:channelId",
                element: <Chat channelType={ChannelType.USER} />,
            },
            {
                path: "channels/g/:channelId",
                element: <Chat channelType={ChannelType.GROUP} />,
            }
        ]
    },
])

if (process.env.NODE_ENV === 'browser_mock') {
    worker.start({ onUnhandledRequest: 'bypass', })
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    <React.StrictMode>
        <Provider store={store}>
            <MantineProvider theme={{
                fontFamily: 'Open Sans, sans-serif',
            }}>
                <Notifications />
                <RouterProvider router={router} />
            </MantineProvider>
        </Provider>
    </React.StrictMode>,
)
