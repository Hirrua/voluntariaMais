"use client"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { useState } from "react"
import { Label } from "@/components/ui/label"

export default function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    return (
        <div className="min-h-screen bg-white flex items-center justify-center">
            <div className="shadow-xl">
                <Card className="p-6">
                    <CardHeader>
                        <CardTitle>Login</CardTitle>
                        <CardDescription>
                            Forneça seu e-mail e sua senha para realizar o login em sua conta
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <form>
                            <div className="flex flex-col gap-6">
                                <div className="grid gap-3">
                                    <Label htmlFor="email">Email</Label>
                                    <Input
                                        id="email"
                                        type="email"
                                        value={email}
                                        onChange={(ev) => setEmail(ev.target.value)}
                                        placeholder="m@example.com"
                                        required
                                    />
                                </div>
                                <div className="grid gap-3">
                                    <div className="flex items-center">
                                        <Label htmlFor="password">Password</Label>
                                    </div>
                                    <Input 
                                        id="password" 
                                        type="password" 
                                        value={password} 
                                        onChange={(ev) => setPassword(ev.target.value)}
                                        required />
                                </div>
                                <div className="flex flex-col gap-3">
                                    <Button type="submit" className="w-full bg-[#8F89FB] hover:bg-[#2A2599]">
                                        Login
                                    </Button>
                                </div>
                            </div>
                            <div className="mt-4 text-center text-sm">
                                Você não possui uma conta?{" "}
                                <a href="/singup" className="underline underline-offset-4">
                                    Cadastre-se
                                </a>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}