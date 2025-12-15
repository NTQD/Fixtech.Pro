import { Controller, Get, UseGuards } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity'; // Local import
import { ApiTags, ApiOperation } from '@nestjs/swagger';

@ApiTags('Users')
@Controller('users')
export class UsersController {
    constructor(
        @InjectRepository(User)
        private usersRepository: Repository<User>,
    ) { }

    @Get()
    @ApiOperation({ summary: 'Get all users (Debug only)' })
    async findAll(): Promise<User[]> {
        return this.usersRepository.find();
    }
}
