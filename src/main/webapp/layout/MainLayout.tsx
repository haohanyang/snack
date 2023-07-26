import { Outlet } from "react-router-dom"
import AppHeader from './Header'
import {
    AppShell, Navbar, Header, Aside, useMantineTheme,
    Divider, Loader, Alert, Center, ScrollArea, TextInput
} from '@mantine/core'
import LeftNav from './LeftNav'
import RightBar from './RightBar'
import UserMenu from '../components/UserMenu'
import { useGetMeQuery } from "../app/api/apiSlice"
import { useState } from "react"

export default function MainLayout() {
    const theme = useMantineTheme()
    const [leftBarOpened, setLeftBarOpened] = useState(true)
    const [rightBarOpened, setRightBarOpened] = useState(false)

    const toggleLeftBar = () => setLeftBarOpened(o => !o)
    const toggleRightBar = () => setRightBarOpened(o => !o)

    const { data: me, isError, isFetching } = useGetMeQuery()

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching || !me) {
        return <Center>
            <Loader />
        </Center>
    }

    return (
        <AppShell
            layout="alt"
            navbarOffsetBreakpoint="sm"
            asideOffsetBreakpoint="sm"
            styles={{
                main: {
                    background: theme.colors.gray[1],
                },
            }}
            navbar={
                <Navbar p="sm" pr={0} hiddenBreakpoint="sm" width={{ sm: 250, lg: 300 }} hidden={!leftBarOpened}>
                    <UserMenu />
                    <Divider my="sm" />
                    <TextInput placeholder="Search" pr="sm" mb="sm" rightSection={<i className="bi bi-search"></i>} />
                    <ScrollArea h={"calc(100vh - 75"}>
                        <LeftNav openChat={() => setLeftBarOpened(false)} />
                    </ScrollArea>
                </Navbar>
            }
            aside={
                <Aside p={0} width={{ sm: 250, lg: 300 }} hiddenBreakpoint="sm" hidden={!rightBarOpened}>
                    <RightBar toggleRightBar={toggleRightBar} />
                </Aside>
            }
            header={
                <Header height={{ base: 72 }} px="sm">
                    <AppHeader
                        toggleLeftBar={toggleLeftBar} toggleRightBar={toggleRightBar}
                    />
                </ Header>
            }
        >
            <Outlet />
        </AppShell>
    )
}
