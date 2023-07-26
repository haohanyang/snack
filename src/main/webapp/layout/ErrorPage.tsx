import { Text } from "@mantine/core"
import { useDocumentTitle } from "@mantine/hooks"

export default function ErrorPage() {
    useDocumentTitle("Error")
    return <Text>An error occurred</Text>
}
