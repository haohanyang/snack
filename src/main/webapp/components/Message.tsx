import { Flex, Avatar, Text, Box, Image } from "@mantine/core"
import { Message } from "../models/message"
import moment from 'moment'


interface MessageProps {
    message: Message
}

export default function MessageComponent({ message }: MessageProps) {
    const { author, content, createdAt, attachmentUri } = message
    const time = moment(createdAt)
    return <Flex>
        <Avatar style={{ marginRight: 10, marginTop: 5 }}
            src={author.avatar} alt="avatar" />
        <Box>
            <Text fw={500}>{author.firstName + " " + author.lastName}</Text>
            {attachmentUri ? <Image src={attachmentUri} radius="md" alt="attachment" sx={{ maxWidth: 300 }} /> :
                <Text fz="sm"> {content} </Text>}
            <Text fz="xs" color="gray">{time.format("D MMM h:mm a")}</Text>
        </Box>
    </Flex>
}