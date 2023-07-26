import { Group, Skeleton, Stack } from "@mantine/core"

export default function StatusSkeleton() {
    return <Group align="start">
        <Skeleton height={50} width={50} circle mb="xl" mt={10} />
        <Stack mt={15}>
            <Skeleton height={10} width={200} radius="xl" />
            <Skeleton height={10} width={200} radius="xl" />
        </Stack>
    </Group>
}