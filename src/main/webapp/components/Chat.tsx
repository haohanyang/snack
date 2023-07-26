import { TextInput, ActionIcon, Loader, Alert, ScrollArea, Center, Box, FileButton } from "@mantine/core"
import { notifications } from '@mantine/notifications'
import { useEffect, useRef, useState } from "react"
import { useParams } from "react-router"
import MessageComponent from "./Message"
import { ChannelInfo, ChannelType } from "../models/channel"
import { useGetMeQuery, useGetChannelMessagesQuery, useSendChannelMessageMutation, apiSlice } from "../app/api/apiSlice"
import { MessageRequest } from "../models/requests"
import { useAppDispatch } from "../app/hooks"
import { setId } from "../app/chat/chatSlice"
import { FileUploadResult } from "../models/file"


interface Props {
    channelType: ChannelType
}

export default function Chat({ channelType }: Props) {

    const dispatch = useAppDispatch()
    const { channelId } = useParams<{ channelId: string }>()

    useEffect(() => {
        if (channelId && /\d+/.test(channelId)) {
            if (channelType == ChannelType.GROUP) {
                dispatch(setId("g" + channelId))
                // Mark messages as read
                dispatch(apiSlice.util.updateQueryData("getChannels", undefined, draft => {
                    const channel = draft.groupChannels.find(c => c.id.toString() === channelId)
                    if (channel) {
                        channel.unreadMessagesCount = 0
                    }
                }))
            } else {
                dispatch(setId("u" + channelId))
                // Mark messages as read
                dispatch(apiSlice.util.updateQueryData("getChannels", undefined, draft => {
                    const channel = draft.userChannels.find(c => c.id.toString() === channelId)
                    if (channel) {
                        channel.unreadMessagesCount = 0
                    }
                }))
            }
        }
    }, [dispatch, channelId, channelType])

    if (!channelId || !/\d+/.test(channelId)) {
        throw new Error("Invalid channel id")
    }

    const channel: ChannelInfo = {
        id: parseInt(channelId),
        type: channelType,
    }

    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })

    const [input, setInput] = useState("")
    const [fileUploadResult, setFileUploadResult] = useState<FileUploadResult | null>(null)

    const [sendChannelMessage, { isLoading }] = useSendChannelMessageMutation()

    const viewport = useRef<HTMLDivElement>(null)

    // @ts-ignore
    const scrollToBottom = () => {
        if (viewport.current) {
            // @ts-ignore
            viewport.current.scrollTo({ top: viewport.current.scrollHeight, behavior: "instant" })
        }
    }

    const { data: messages = [], isFetching, isError, isSuccess } =
        useGetChannelMessagesQuery(channel)

    const uploadFile = async (file: File | null) => {
        if (file && me) {
            if (file.name.length > 80) {
                notifications.show({
                    title: "Error",
                    message: "File name must not be longer than 80 characters",
                    color: "red",
                })
                return
            }
            if (file.size > 10 * 1024 * 1024) {
                notifications.show({
                    title: "Error",
                    message: "File size must be less than 10MB",
                    color: "red",
                })
                return
            }

            const formData = new FormData()

            formData.append("file", file)
            formData.append("user", me.id.toString())

            try {
                const response = await fetch("/api/v1/files", {
                    method: "POST",
                    body: formData,
                })
                if (response.ok) {
                    const result: FileUploadResult = await response.json()
                    if (process.env.NODE_ENV === "browser_mock") {
                        // TODO: revoke object url
                        result.key = URL.createObjectURL(file)
                    }
                    setFileUploadResult(result)
                } else {
                    notifications.show({
                        title: "Error",
                        message: "Failed to upload the file",
                        color: "red",
                    })
                }
            } catch (err) {
                notifications.show({
                    title: "Error",
                    message: "Failed to upload the file",
                    color: "red",
                })
            }
        }
    }
    const sendMessage = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        if (!isLoading && me) {

            if (!fileUploadResult) {
                if (input.trim().length === 0) {
                    return
                }

                if (input.trim().length > 255) {
                    notifications.show({
                        title: "Error",
                        message: "Message must be less than 255 characters",
                        color: "red",
                    })
                    return
                }
            }

            const request: MessageRequest = {
                channel: channel,
                content: fileUploadResult ? fileUploadResult.fileName : input.trim(),
                authorId: me.id,
                fileUploadResult: fileUploadResult,
            }
            try {
                await sendChannelMessage(request).unwrap()
                setInput("")
                setFileUploadResult(null)
            } catch (err) {
                notifications.show({
                    title: "Error",
                    message: "Failed to send the message",
                    color: "red",
                })
            }
        }
    }

    let content: JSX.Element | null = null
    if (isError) {
        content = <Alert color="red">An error occurred</Alert>
    }

    if (isFetching) {
        content = <Center>
            <Loader />
        </Center>
    }

    if (isSuccess && messages.length > 0) {
        scrollToBottom()
    }

    content = <>
        {messages.map(message => <MessageComponent key={message.id} message={message} />)}
    </>

    return <>
        <ScrollArea h={"calc(100vh - 165px)"} viewportRef={viewport}>
            {content}
        </ScrollArea>
        <Box component="form" onSubmit={sendMessage}>
            <TextInput
                value={fileUploadResult ? fileUploadResult.fileName : input}
                onChange={event => fileUploadResult === null && setInput(event.currentTarget.value)}
                pt="sm"
                radius="xl"
                size="md"
                rightSection={
                    <>
                        {fileUploadResult ?
                            <ActionIcon type="button" size={32} color="blue" radius="xl" variant="filled" mr={5} onClick={() => { setFileUploadResult(null); setInput("") }}>
                                <i className="bi bi-x-lg"></i>
                            </ActionIcon> :
                            <FileButton accept="image/png,image/jpeg" onChange={uploadFile} >
                                {props => <ActionIcon {...props} type="button" size={32} color="blue" radius="xl" variant="filled" mr={5}
                                >
                                    <i className="bi bi-paperclip"></i>
                                </ActionIcon>}
                            </FileButton>}
                        <ActionIcon type="submit" size={32} color="blue" radius="xl" variant="filled" mr={35}
                        >
                            <i className="bi bi-send"></i>
                        </ActionIcon>
                    </>
                }
                rightSectionWidth={42}
                required
            />
        </Box>
    </>
}
