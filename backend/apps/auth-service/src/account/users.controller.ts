import { Controller, Get, Param, Query, NotFoundException, Body, Patch, Delete, BadRequestException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity'; // Local import
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import * as bcrypt from 'bcrypt';

@ApiTags('Users')
@Controller('users')
export class UsersController {
    constructor(
        @InjectRepository(User)
        private usersRepository: Repository<User>,
    ) { }

    @Get()
    @ApiOperation({ summary: 'Get all users with optional role filter' })
    async findAll(@Query('role') role?: string): Promise<User[]> {
        if (role) {
            return this.usersRepository.find({ where: { role: role as any } });
        }
        return this.usersRepository.find();
    }

    @Get(':id')
    @ApiOperation({ summary: 'Get user by ID' })
    async findOne(@Param('id') id: number): Promise<User> {
        const user = await this.usersRepository.findOne({ where: { id } });
        if (!user) {
            throw new NotFoundException(`User with ID ${id} not found`);
        }
        return user;
    }

    @Patch(':id/role')
    @ApiOperation({ summary: 'Update user role' })
    async updateRole(@Param('id') id: number, @Body('role') role: string) {
        const user = await this.usersRepository.findOne({ where: { id } });
        if (!user) throw new NotFoundException(`User not found`);

        user.role = role as any;
        return this.usersRepository.save(user);
    }

    @Patch(':id')
    @ApiOperation({ summary: 'Update user profile' })
    async updateProfile(@Param('id') id: number, @Body() body: any) {
        const user = await this.usersRepository.findOne({ where: { id } });
        if (!user) throw new NotFoundException(`User not found`);

        if (body.full_name) user.full_name = body.full_name;
        if (body.phone) user.phone = body.phone;
        if (body.avatar_url) user.avatar_url = body.avatar_url;

        return this.usersRepository.save(user);
    }

    @Patch(':id/password')
    @ApiOperation({ summary: 'Change user password' })
    async changePassword(@Param('id') id: number, @Body() body: any) {
        const { currentPassword, newPassword } = body;
        const user = await this.usersRepository.findOne({ where: { id } });
        if (!user) throw new NotFoundException(`User not found`);

        // Verify current password
        // Dynamically import bcrypt to avoid require issues if not globally available, or just use * as bcrypt
        // Assuming * as bcrypt is already valid or I need to add the import at top.
        // Actually, I'll add the import at the top in a separate chunk to be safe.
        // For this chunk, I will use 'bcrypt' assuming it's imported.

        const isMatch = await bcrypt.compare(currentPassword, user.password_hash);
        if (!isMatch) {
            throw new BadRequestException('Mật khẩu hiện tại không đúng');
        }

        const salt = await bcrypt.genSalt();
        user.password_hash = await bcrypt.hash(newPassword, salt);

        return this.usersRepository.save(user);
    }

    @Delete(':id')
    @ApiOperation({ summary: 'Ban user (Soft delete)' })
    async banUser(@Param('id') id: number) {
        const user = await this.usersRepository.findOne({ where: { id } });
        if (!user) throw new NotFoundException(`User not found`);

        user.status = 0; // Ban (0 = Banned, 1 = Active)
        return this.usersRepository.save(user);
    }
}
