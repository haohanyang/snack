import { Alert, Center, Grid, Loader, Group, TextInput, Text, Textarea, rem, FileButton, Image, Button, Avatar, Modal, UnstyledButton, Divider } from "@mantine/core"
import { useGetMeQuery, useUpdateMeMutation } from "../app/api/apiSlice"
import React, { useEffect, useState } from "react"
import { notifications } from "@mantine/notifications"
import { FileUploadResult } from "../models/file"
import { UpdateProfileRequest } from "../models/requests"

interface ProfileSettingModalProps {
    opened: boolean
    onClose: () => void
}

export default function ProfileSettingModal({ opened, onClose }: ProfileSettingModalProps) {

    const { data: me, isLoading, isError, isSuccess } = useGetMeQuery()

    const [firstName, setFirstName] = useState<string>("")
    const [lastName, setLastName] = useState<string>("")
    const [status, setStatus] = useState<string>("")
    const [bio, setBio] = useState<string>("")
    const [error, setError] = useState<string | null>(null)

    const [avatarUploadResult, setAvatarUploadResult] = useState<FileUploadResult | null>(null)
    const [backgroundUploadResult, setBackgroundUploadResult] = useState<FileUploadResult | null>(null)
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null)
    const [backgroundUrl, setBackgroundUrl] = useState<string | null>(null)

    const [updateProfile, { isLoading: f }] = useUpdateMeMutation()

    const uploadAvatar = async (avatarFile: File | null) => {
        if (avatarFile && me) {
            if (avatarFile.name.length > 80) {
                notifications.show({
                    title: "Error",
                    message: "File name must not be longer than 80 characters",
                    color: "red",
                })
                return
            }

            if (avatarFile.size > 1024 * 1024 * 10) {
                notifications.show({
                    title: "File too large",
                    message: "File size must be less than 10MB",
                    color: "red",
                    autoClose: 5000,
                })
                return
            }
            const form = new FormData()
            form.append("file", avatarFile)

            try {
                const response = await fetch("/api/v1/files", {
                    method: "POST",
                    body: form,
                })
                if (response.ok) {
                    const result: FileUploadResult = await response.json()
                    if (process.env.NODE_ENV === "browser_mock") {
                        const uri = URL.createObjectURL(avatarFile)
                        setAvatarUploadResult({ ...result, key: uri })
                        setAvatarUrl(uri)
                    } else {
                        setAvatarUploadResult(result)
                        setAvatarUrl(result.uri)
                    }
                } else {
                    notifications.show({
                        title: "Error",
                        message: "Failed to upload the avatar",
                        color: "red",
                    })
                }
            } catch (error) {
                notifications.show({
                    title: "Error",
                    message: "Failed to upload the avatar",
                    color: "red",
                })
            }
        }
    }

    const uploadBackgroundImage = async (backgroundFile: File | null) => {
        if (backgroundFile && me) {
            if (backgroundFile.size > 1024 * 1024 * 10) {
                notifications.show({
                    title: "File too large",
                    message: "File size must be less than 10MB",
                    color: "red",
                })
                return
            }
            const form = new FormData()
            form.append("file", backgroundFile)

            try {
                const response = await fetch("/api/v1/files", {
                    method: "POST",
                    body: form,
                })
                if (response.ok) {
                    const result: FileUploadResult = await response.json()
                    if (process.env.NODE_ENV === "browser_mock") {
                        const url = URL.createObjectURL(backgroundFile)
                        setBackgroundUploadResult({ ...result, key: url })
                        setBackgroundUrl(url)
                    } else {
                        setBackgroundUploadResult(result)
                        setBackgroundUrl(result.uri)
                    }
                } else {
                    notifications.show({
                        title: "Error",
                        message: "Failed to upload the background image",
                        color: "red",
                    })
                }
            } catch (error) {
                notifications.show({
                    title: "Error",
                    message: "Failed to upload the background image",
                    color: "red",
                })
            }
        }
    }

    const submitUpdate = async (e: React.FormEvent) => {
        e.preventDefault()
        if (me && !f) {
            try {
                const request: UpdateProfileRequest = {
                    userId: me.id,
                    firstName: firstName,
                    lastName: lastName,
                    status: status,
                    bio: bio,
                    avatar: avatarUploadResult,
                    backgroundImage: backgroundUploadResult,
                }
                await updateProfile(request).unwrap()
                onClose()
            } catch (err) {
                setError("Failed to update profile")
            }
        }

    }

    useEffect(() => {
        if (isSuccess && me) {
            setFirstName(me.firstName)
            setLastName(me.lastName)
            setStatus(me.status)
            setBio(me.bio)
            setAvatarUrl(me.avatar)
            setBackgroundUrl(me.backgroundImage)
        }
    }, [isSuccess])


    if (isError) {
        return <Alert>Error</Alert>
    }

    if (isLoading || !me) {
        return <Center><Loader /></Center>
    }


    return <Modal title={<Text>Setting</Text>} opened={opened} onClose={onClose} > <form onSubmit={submitUpdate}>
        <Grid>
            <Grid.Col span={6}>
                <TextInput label="First name" value={firstName} onChange={e => setFirstName(e.target.value)} />
            </Grid.Col>
            <Grid.Col span={6}>
                <TextInput label="Last name" value={lastName} onChange={e => setLastName(e.target.value)} />
            </Grid.Col>
        </Grid>
        <TextInput label="Status" value={status} onChange={e => setStatus(e.target.value)} />
        <Textarea label="Bio" value={bio} onChange={e => setBio(e.target.value)} />
        <Divider my="sm" />
        <FileButton onChange={uploadBackgroundImage} accept="image/png,image/jpeg">
            {(props) => <UnstyledButton {...props} display="block" w="100%">
                <Image src={backgroundUrl} height={200} >
                </Image>
            </UnstyledButton>}
        </FileButton>

        <FileButton onChange={f => uploadAvatar(f)} accept="image/png,image/jpeg">
            {(props) => <UnstyledButton {...props}>
                <Avatar src={avatarUrl} size={80} radius={80} mt={-30} ml="md" sx={{ border: `${rem(2)} solid white` }} alt="avatar" /></UnstyledButton>}
        </FileButton>


        <Divider my="sm" />
        {error && <Alert color="red">{error}</Alert>}
        <Group position="right">
            <Button type="submit">Save</Button>
        </Group>
    </form>
    </Modal>
}