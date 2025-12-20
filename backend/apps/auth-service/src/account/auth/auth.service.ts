import { Injectable, UnauthorizedException, BadRequestException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcrypt';
import { User, UserRole } from '../entities/user.entity'; // Local import

@Injectable()
export class AuthService {
    constructor(
        @InjectRepository(User)
        private usersRepository: Repository<User>,
        private jwtService: JwtService,
    ) { }

    async register(registerDto: any): Promise<User> {
        const { email, password, full_name, phone, avatar_url } = registerDto;

        const existingUser = await this.usersRepository.findOne({ where: { email } });
        if (existingUser) {
            throw new BadRequestException('Email already exists');
        }

        const salt = await bcrypt.genSalt();
        const password_hash = await bcrypt.hash(password, salt);

        const user = this.usersRepository.create({
            email,
            password_hash,
            full_name,
            phone,
            role: UserRole.CUSTOMER,
            status: 1, // Default Active
            avatar_url: avatar_url || null
        });

        return this.usersRepository.save(user);
    }

    async validateUser(email: string, pass: string): Promise<any> {
        const user = await this.usersRepository.findOne({ where: { email } });
        if (user && (await bcrypt.compare(pass, user.password_hash))) {
            const { password_hash, ...result } = user;
            return result;
        }
        return null;
    }

    async login(user: any) {
        if (user.status === 0) {
            throw new UnauthorizedException('Account is banned');
        }

        const payload = { email: user.email, sub: user.id, role: user.role };
        return {
            access_token: this.jwtService.sign(payload),
            user: {
                id: user.id,
                email: user.email,
                full_name: user.full_name,
                role: user.role,
                avatar_url: user.avatar_url,
                status: user.status,
                phone: user.phone,
                created_at: user.created_at
            }
        };
    }
}
