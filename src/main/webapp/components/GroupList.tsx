import { Avatar, NavLink, Box, Center, Loader, Alert, ActionIcon, ScrollArea, TextInput, Divider, Text, Group, Popover, MultiSelect, Button } from "@mantine/core"
import { Link, useNavigate } from "react-router-dom"
import { useGetFriendsQuery, useGetGroupChannelsQuery, useGetMeQuery, useAddNewGroupChannelMutation } from "../app/api/apiSlice"
import { FormEvent, forwardRef, useEffect, useState } from "react"
import User from "../models/user"
import { GroupChannelRequest } from "../models/requests"
import { useAppDispatch } from "../app/hooks"
import { reset } from "../app/chat/chatSlice"

interface ItemProps extends React.ComponentPropsWithoutRef<"div"> {
    image: string
    label: string
    description: string
    value: string
}

interface CreateGroupPanelProps {
    me: User
}

export default function GroupList() {

    const dispatch = useAppDispatch()
    useEffect(() => {
        dispatch(reset())
    }, [dispatch])

    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })

    const { data: channels = [], isFetching, isError } = useGetGroupChannelsQuery()

    if (isError) {
        return <Alert color="red">Error</Alert>
    }

    if (isFetching || !me) {
        return <Center>
            <Loader />
        </Center>
    }

    return <Box>
        <TextInput mb="sm" placeholder="Search" rightSection={<i className="bi bi-search"></i>} />
        <Group position="apart" px="sm">
            <Text tt="uppercase" fz="sm">{"All groups -" + channels.length}</Text>
            <CreateGroupPanel me={me} />
        </Group>
        <Divider m="xs" />
        <ScrollArea h={"calc(100vh - 200px)"}>
            {
                channels.map(channel => {
                    return <NavLink
                        key={channel.id}
                        component={Link}
                        to={"/chat/channels/g/" + channel.id}
                        label={channel.name}
                        description={channel.memberCount + " members"}
                        icon={<Avatar color="blue"><i className="bi bi-people-fill"></i></Avatar>}
                        rightSection={
                            <ActionIcon variant="transparent">
                                <i className="bi bi-chat-left-fill"></i>
                            </ActionIcon>}
                    />
                })
            }
        </ScrollArea>
    </Box>

}

function CreateGroupPanel({ me }: CreateGroupPanelProps) {

    const selectItem = forwardRef<HTMLDivElement, ItemProps>(
        ({ image, label, description, ...others }: ItemProps, ref) => (
            <div ref={ref} {...others}>
                <Group noWrap>
                    <Avatar src={image} />
                    <div>
                        <Text>{label}</Text>
                        <Text size="xs" color="dimmed">
                            {description}
                        </Text>
                    </div>
                </Group>
            </div>
        )
    )
    const navigate = useNavigate()
    const [values, setValues] = useState<string[]>([])
    const [groupName, setGroupName] = useState<string>("")
    const [groupDescription, setGroupDescription] = useState<string>("")
    const { data: friends = [], isFetching, isError } = useGetFriendsQuery()
    const [addNewGroupChannel, { isLoading }] = useAddNewGroupChannelMutation()

    const createGroupChannel = async (e: FormEvent) => {
        e.preventDefault()
        if (!isLoading) {
            try {
                const groupChannelRequest: GroupChannelRequest = {
                    name: groupName,
                    description: groupDescription,
                    memberIds: values,
                    creatorId: me.id
                }
                const channel = await addNewGroupChannel(groupChannelRequest).unwrap()
                navigate("/chat/channels/g/" + channel.id)
            } catch (error) {
                console.log(error)
            }
        }
    }


    if (isError) {
        return <Alert color="red">Error</Alert>
    }

    if (isFetching) {
        return <Center>
            <Loader />
        </Center>
    }

    return <Popover width={300} position="bottom" withArrow shadow="md">
        <Popover.Target>
            <ActionIcon>
                <i className="bi bi-plus-circle"></i>
            </ActionIcon>
        </Popover.Target>
        <Popover.Dropdown>
            <Box component="form" onSubmit={createGroupChannel}>
                <Text>New Group</Text>
                <Divider />
                <TextInput mb="sm" placeholder="Fan group" label="Group name"
                    value={groupName} onChange={e => setGroupName(e.target.value)} required />
                <TextInput mb="sm" placeholder="A place for fans" label="Group description"
                    value={groupDescription} onChange={e => setGroupDescription(e.target.value)} required />
                <MultiSelect
                    label="Add friends"
                    placeholder="Add friends"
                    itemComponent={selectItem}
                    value={values}
                    onChange={setValues}
                    required
                    data={friends.map(friend => {
                        const item: ItemProps = {
                            image: friend.avatar,
                            label: friend.firstName + " " + friend.lastName,
                            description: "@" + friend.username,
                            value: friend.id.toString()
                        }
                        return item
                    })} />
                <Button type="submit" fullWidth mt="sm" color="blue">Create</Button>

            </Box>
        </Popover.Dropdown>
    </Popover>
}